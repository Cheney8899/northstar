package org.dromara.northstar.support.notification;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.dromara.northstar.common.event.NorthstarEvent;
import org.dromara.northstar.common.event.NorthstarEventType;
import org.dromara.northstar.common.model.MailConfigDescription;
import org.dromara.northstar.strategy.IMessageSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.extern.slf4j.Slf4j;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.NoticeField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TradeField;

@Slf4j
public class MailDeliveryManager implements IMessageSenderManager{
	
	private MailConfigDescription emailConfig;
	
	private JavaMailSender sender;
	
	private MailSenderFactory factory;
	
	private IMailMessageContentHandler contentHandler;
	
	private EnumSet<NorthstarEventType> interestedEvents = EnumSet.noneOf(NorthstarEventType.class);
	
	private static final String UTF8 = "UTF-8";
	
	private boolean enabled;
	
	private Executor exec = Executors.newSingleThreadExecutor(); //专门用于发送邮件，理论上不应该有很多任务，所以单个线程足够
	
	public MailDeliveryManager(MailSenderFactory factory, IMailMessageContentHandler contentHandler) {
		this.factory = factory;
		this.contentHandler = contentHandler;
	}
	
	public void setEmailConfig(MailConfigDescription emailConfig) {
		this.emailConfig = emailConfig;
		this.enabled = !emailConfig.isDisabled();
		this.sender = factory.newInstance(emailConfig);
		interestedEvents.clear();
		emailConfig.getInterestTopicList().stream().forEach(interestedEvents::add);
	}
	
	@Override
	public List<String> getSubscribers(){
		if(Objects.isNull(emailConfig)) {
			return Collections.emptyList();
		}
		return emailConfig.getSubscriberList();
	}
	
	public void onEvent(NorthstarEvent event) {
		if(!enabled || !interestedEvents.contains(event.getEvent())
				|| event.getData() instanceof OrderField order && order.getOrderStatus() == OrderStatusEnum.OS_AllTraded) {
			return;
		}
		String title = switch(event.getEvent()) {
		case LOGGED_IN -> "网关连线提示";
		case LOGGED_OUT -> "网关断线提示";
		case TRADE -> "成交提示";
		case ORDER -> "订单提示";
		case NOTICE -> "消息提示";
		default -> throw new IllegalArgumentException("Unexpected value: " + event.getEvent());
		};
		
		String content = switch(event.getEvent()) {
		case LOGGED_IN -> contentHandler.onConnected((String) event.getData());
		case LOGGED_OUT -> contentHandler.onDisconnected((String) event.getData());
		case TRADE -> contentHandler.onEvent((TradeField) event.getData());
		case ORDER -> contentHandler.onEvent((OrderField) event.getData());
		case NOTICE -> contentHandler.onEvent((NoticeField) event.getData());
		default -> throw new IllegalArgumentException("Unexpected value: " + event.getEvent());
		};
		exec.execute(() -> {
			MimeMessageHelper msg = new MimeMessageHelper(sender.createMimeMessage(), UTF8);
			try {
				msg.setSubject("Northstar" + title);
				msg.setFrom(emailConfig.getEmailUsername());
				for(String mailTo : emailConfig.getSubscriberList()) {
					msg.addTo(mailTo);
				}
				msg.setText(content);
				sender.send(msg.getMimeMessage());
			} catch (Exception e) {
				log.error("邮件发送异常", e);
			}
		});
	}

	@Override
	public IMessageSender getSender() {
		return new IMessageSender() {
			
			@Override
			public void send(String receiver, String content) {
				exec.execute(() -> {
					MimeMessageHelper msg = new MimeMessageHelper(sender.createMimeMessage(), UTF8);
					try {
						msg.setSubject("Northstar消息");
						msg.setFrom(emailConfig.getEmailUsername());
						msg.setTo(receiver);
						msg.setText(content);
						sender.send(msg.getMimeMessage());
					} catch (Exception e) {
						log.error("邮件发送异常", e);
					}
				});
			}
			
			@Override
			public void send(String receiver, String title, String content) {
				exec.execute(() -> {
					MimeMessageHelper msg = new MimeMessageHelper(sender.createMimeMessage(), UTF8);
					try {
						msg.setSubject("Northstar信息：" + title);
						msg.setFrom(emailConfig.getEmailUsername());
						msg.setTo(receiver);
						msg.setText(content);
						sender.send(msg.getMimeMessage());
					} catch (Exception e) {
						log.error("邮件发送异常", e);
					}
				});				
			}
		};
	}
	
}
