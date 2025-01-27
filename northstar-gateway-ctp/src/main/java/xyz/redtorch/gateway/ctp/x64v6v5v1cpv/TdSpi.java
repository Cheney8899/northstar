package xyz.redtorch.gateway.ctp.x64v6v5v1cpv;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.northstar.common.constant.ChannelType;
import org.dromara.northstar.common.constant.ConnectionState;
import org.dromara.northstar.common.constant.DateTimeConstant;
import org.dromara.northstar.common.event.NorthstarEventType;
import org.dromara.northstar.common.exception.NoSuchElementException;
import org.dromara.northstar.gateway.Contract;
import org.dromara.northstar.gateway.common.GatewayAbstract;
import org.dromara.northstar.gateway.ctp.CtpContract;
import org.dromara.northstar.gateway.ctp.CtpSimGatewaySettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.redtorch.gateway.ctp.common.CtpContractNameResolver;
import xyz.redtorch.gateway.ctp.common.GatewayConstants;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcAccountregisterField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcBatchOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcBrokerTradingAlgosField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcBrokerTradingParamsField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcBulletinField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcCFMMCTradingAccountKeyField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcCFMMCTradingAccountTokenField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcCancelAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcChangeAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcCombActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcCombInstrumentGuardField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcContractBankField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcDepthMarketDataField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcEWarrantOffsetField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcErrorConditionalOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExchangeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExchangeMarginRateAdjustField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExchangeMarginRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExchangeRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExecOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcExecOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcForQuoteField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcForQuoteRspField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputBatchOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputCombActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputExecOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputExecOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputForQuoteField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputOptionSelfCloseActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputOptionSelfCloseField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputQuoteActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInputQuoteField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInstrumentCommissionRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInstrumentField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInstrumentMarginRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInstrumentOrderCommRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInstrumentStatusField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestUnitField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestorField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestorPositionCombineDetailField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestorPositionDetailField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestorPositionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcInvestorProductGroupMarginField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcMMInstrumentCommissionRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcMMOptionInstrCommRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcNoticeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcNotifyQueryAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOpenAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOptionInstrCommRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOptionInstrTradeCostField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOptionSelfCloseActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOptionSelfCloseField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcParkedOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcParkedOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcProductExchRateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcProductField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcProductGroupField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQryInstrumentField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQryInvestorField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQryInvestorPositionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQryTradingAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQueryCFMMCTradingAccountTokenField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQuoteActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcQuoteField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRemoveParkedOrderActionField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRemoveParkedOrderField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcReqAuthenticateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcReqQueryAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcReqRepealField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcReqTransferField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcReqUserLoginField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRspAuthenticateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRspInfoField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRspRepealField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRspTransferField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcRspUserLoginField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcSecAgentACIDMapField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcSecAgentCheckModeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcSettlementInfoConfirmField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcSettlementInfoField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTraderApi;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTraderSpi;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradingAccountField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradingAccountPasswordUpdateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradingCodeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradingNoticeField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTradingNoticeInfoField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTransferBankField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcTransferSerialField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcUserLogoutField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.CThostFtdcUserPasswordUpdateField;
import xyz.redtorch.gateway.ctp.x64v6v5v1cpv.api.jctpv6v5v1cpx64apiConstants;
import xyz.redtorch.pb.CoreEnum.CommonStatusEnum;
import xyz.redtorch.pb.CoreEnum.ContingentConditionEnum;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.ExchangeEnum;
import xyz.redtorch.pb.CoreEnum.ForceCloseReasonEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.OptionsTypeEnum;
import xyz.redtorch.pb.CoreEnum.OrderPriceTypeEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreEnum.PriceSourceEnum;
import xyz.redtorch.pb.CoreEnum.ProductClassEnum;
import xyz.redtorch.pb.CoreEnum.TimeConditionEnum;
import xyz.redtorch.pb.CoreEnum.TradeTypeEnum;
import xyz.redtorch.pb.CoreEnum.VolumeConditionEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.NoticeField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

public class TdSpi extends CThostFtdcTraderSpi {

	private static final String MKT_GATEWAY_ID = "CTP_SIM";
	
	private static final Logger logger = LoggerFactory.getLogger(TdSpi.class);

	private GatewayAbstract gatewayAdapter;
	private String logInfo;
	private String gatewayId;
	private CtpSimGatewaySettings settings;

	private String investorName = "";

	private HashMap<String, PositionField.Builder> positionBuilderMap = new HashMap<>();

	private Map<String, String> orderIdToAdapterOrderIdMap = new ConcurrentHashMap<>(50000);
	private Map<String, String> orderIdToOrderRefMap = new ConcurrentHashMap<>(50000);
	private Map<String, String> orderIdToOriginalOrderIdMap = new HashMap<>();
	private Map<String, String> originalOrderIdToOrderIdMap = new HashMap<>();

	private Map<String, String> exchangeIdAndOrderSysIdToOrderIdMap = new ConcurrentHashMap<>(50000);

	private Map<String, SubmitOrderReqField> orderIdToSubmitOrderReqMap = new HashMap<>();
	private Map<String, OrderField> orderIdToOrderMap = new ConcurrentHashMap<>(50000);

	private Lock submitOrderLock = new ReentrantLock();

	private Thread intervalQueryThread;

	TdSpi(GatewayAbstract gatewayAdapter) {
		this.gatewayAdapter = gatewayAdapter;
		this.settings = (CtpSimGatewaySettings) gatewayAdapter.gatewayDescription().getSettings();
		this.gatewayId = gatewayAdapter.gatewayId();
		this.logInfo = "交易网关ID-[" + this.gatewayId + "] [→] ";
		logger.info("当前TdApi版本号：{}", CThostFtdcTraderApi.GetApiVersion());
	}
	
	private CThostFtdcTraderApi cThostFtdcTraderApi;

	private boolean loginStatus = false; // 登陆状态
	private String tradingDay;

	private boolean instrumentQueried = false;
	private boolean investorNameQueried = false;

	private Random random = new Random();
	private AtomicInteger reqId = new AtomicInteger(random.nextInt(1800) % (1800 - 200 + 1) + 200); // 操作请求编号
	private volatile int orderRef = random.nextInt(1800) % (1800 - 200 + 1) + 200; // 订单编号

	private boolean loginFailed = false; // 是否已经使用错误的信息尝试登录过

	private int frontId = 0; // 前置机编号
	private int sessionId = 0; // 会话编号

	private List<OrderField.Builder> orderBuilderCacheList = new LinkedList<>(); // 登录起始阶段缓存Order
	private List<TradeField.Builder> tradeBuilderCacheList = new LinkedList<>(); // 登录起始阶段缓存Trade

	private void startIntervalQuery() {
		if (this.intervalQueryThread != null) {
			logger.error("{}定时查询线程已存在,首先终止", logInfo);
			stopQuery();
		}
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted() && loginStatus) {
				try {
					if (cThostFtdcTraderApi == null) {
						logger.error("{}定时查询线程检测到API实例不存在,退出", logInfo);
						break;
					}
					queryAccount();
					Thread.sleep(1250);
					queryPosition();
					Thread.sleep(1250);
				} catch (InterruptedException e) {
					logger.warn("{}定时查询线程睡眠时检测到中断,退出线程", logInfo, e);
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					logger.error("{}定时查询线程发生异常", logInfo, e);
				}
			}
			if (!loginStatus) {
				logger.warn("{}尚未登陆,跳过查询", logInfo);
			}
		}).start();
	}

	private void stopQuery() {
		try {
			if (intervalQueryThread != null && !intervalQueryThread.isInterrupted()) {
				intervalQueryThread.interrupt();
				intervalQueryThread = null;
			}
		} catch (Exception e) {
			logger.error(logInfo + "停止线程发生异常", e);
		}
	}

	public void connect() {
		if (isConnected() || gatewayAdapter.getConnectionState() == ConnectionState.CONNECTING) {
			logger.warn("{}交易接口已经连接或正在连接，不再重复连接", logInfo);
			return;
		}
		
		if (gatewayAdapter.getConnectionState() == ConnectionState.CONNECTED) {
			reqAuth();
			return;
		}
		gatewayAdapter.setConnectionState(ConnectionState.CONNECTING);
		loginStatus = false;
		instrumentQueried = false;
		investorNameQueried = false;

		if (cThostFtdcTraderApi != null) {
			try {
				CThostFtdcTraderApi cThostFtdcTraderApiForRelease = cThostFtdcTraderApi;
				cThostFtdcTraderApi = null;
				cThostFtdcTraderApiForRelease.RegisterSpi(null);

				new Thread(() -> {
					try {
						logger.warn("交易接口异步释放启动！");
						cThostFtdcTraderApiForRelease.Release();
						logger.warn("交易接口异步释放完成！");
					} catch (Throwable t) {
						logger.error("交易接口异步释放发生异常！", t);
					}
				}).start();
				Thread.sleep(100);
			} catch (Throwable t) {
				logger.warn("{}交易接口连接前释放异常", logInfo, t);
			}

		}

		logger.warn("{}交易接口实例初始化", logInfo);
		String envTmpDir = System.getProperty("java.io.tmpdir");
		String tempFilePath = envTmpDir + File.separator + "xyz" + File.separator + "redtorch" + File.separator + "gateway" + File.separator + "ctp" + File.separator + "jctpv6v5v1cpx64api"
				+ File.separator + "CTP_FLOW_TEMP" + File.separator + "TD_" + gatewayId;
		File tempFile = new File(tempFilePath);
		if (!tempFile.getParentFile().exists()) {
			try {
				FileUtils.forceMkdirParent(tempFile);
				logger.info("{}交易接口创建临时文件夹 {}", logInfo, tempFile.getParentFile().getAbsolutePath());
			} catch (IOException e) {
				logger.error("{}交易接口创建临时文件夹失败{}", logInfo, tempFile.getParentFile().getAbsolutePath(), e);
			}
		}

		logger.warn("{}交易接口使用临时文件夹{}", logInfo, tempFile.getParentFile().getAbsolutePath());

		try {
			String tdHost = GatewayConstants.SMART_CONNECTOR.bestEndpoint(settings.getBrokerId());
			String tdPort = settings.getTdPort();
			logger.info("{}使用IP [{}] 连接交易网关", logInfo, tdHost);
			cThostFtdcTraderApi = CThostFtdcTraderApi.CreateFtdcTraderApi(tempFile.getAbsolutePath());
			cThostFtdcTraderApi.RegisterSpi(this);
			cThostFtdcTraderApi.RegisterFront("tcp://" + tdHost + ":" + tdPort);
			cThostFtdcTraderApi.Init();
		} catch (Throwable t) {
			logger.error("{}交易接口连接异常", logInfo, t);
		}

		new Thread(() -> {
			try {
				Thread.sleep(60000);
				if (!(isConnected() && investorNameQueried && instrumentQueried)) {
					logger.error("{}交易接口连接超时,尝试断开", logInfo);
					gatewayAdapter.disconnect();
				}
			} catch (Throwable t) {
				logger.error("{}交易接口处理连接超时线程异常", logInfo, t);
			}
		}).start();
	}

	public void disconnect() {
		try {
			this.stopQuery();
			if (cThostFtdcTraderApi != null && gatewayAdapter.getConnectionState() != ConnectionState.DISCONNECTING) {
				logger.warn("{}交易接口实例开始关闭并释放", logInfo);
				loginStatus = false;
				instrumentQueried = false;
				investorNameQueried = false;
				gatewayAdapter.setConnectionState(ConnectionState.DISCONNECTING);
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.LOGGING_OUT, gatewayId);
				try {
					if (cThostFtdcTraderApi != null) {
						CThostFtdcTraderApi cThostFtdcTraderApiForRelease = cThostFtdcTraderApi;
						cThostFtdcTraderApi = null;
						cThostFtdcTraderApiForRelease.RegisterSpi(null);

						new Thread(() -> {
							try {
								logger.warn("交易接口异步释放启动！");
								cThostFtdcTraderApiForRelease.Release();
								logger.warn("交易接口异步释放完成！");
							} catch (Throwable t) {
								logger.error("交易接口异步释放发生异常！", t);
							}
						}).start();
					}
					Thread.sleep(100);
				} catch (Throwable t) {
					logger.error("{}交易接口实例关闭并释放异常", logInfo, t);
				}
				gatewayAdapter.setConnectionState(ConnectionState.DISCONNECTED);
				logger.warn("{}交易接口实例关闭并异步释放", logInfo);
			} else {
				logger.warn("{}交易接口实例不存在或正在关闭释放,无需操作", logInfo);
			}
		} catch (Throwable t) {
			logger.error("{}交易接口实例关闭并释放异常", logInfo, t);
		}

	}

	public boolean isConnected() {
		return gatewayAdapter.getConnectionState() == ConnectionState.CONNECTED && loginStatus;
	}

	public String getTradingDay() {
		return tradingDay;
	}

	public void queryAccount() {
		if (cThostFtdcTraderApi == null) {
			logger.warn("{}交易接口尚未初始化,无法查询账户", logInfo);
			return;
		}
		if (!loginStatus) {
			logger.warn("{}交易接口尚未登录,无法查询账户", logInfo);
			return;
		}
		if (!instrumentQueried) {
			logger.warn("{}交易接口尚未获取到合约信息,无法查询账户", logInfo);
			return;
		}
		if (!investorNameQueried) {
			logger.warn("{}交易接口尚未获取到投资者姓名,无法查询账户", logInfo);
			return;
		}
		try {
			CThostFtdcQryTradingAccountField cThostFtdcQryTradingAccountField = new CThostFtdcQryTradingAccountField();
			cThostFtdcTraderApi.ReqQryTradingAccount(cThostFtdcQryTradingAccountField, reqId.incrementAndGet());
		} catch (Throwable t) {
			logger.error("{}交易接口查询账户异常", logInfo, t);
		}

	}

	public void queryPosition() {
		if (cThostFtdcTraderApi == null) {
			logger.warn("{}交易接口尚未初始化,无法查询持仓", logInfo);
			return;
		}
		if (!loginStatus) {
			logger.warn("{}交易接口尚未登录,无法查询持仓", logInfo);
			return;
		}

		if (!instrumentQueried) {
			logger.warn("{}交易接口尚未获取到合约信息,无法查询持仓", logInfo);
			return;
		}
		if (!investorNameQueried) {
			logger.warn("{}交易接口尚未获取到投资者姓名,无法查询持仓", logInfo);
			return;
		}

		try {
			CThostFtdcQryInvestorPositionField cThostFtdcQryInvestorPositionField = new CThostFtdcQryInvestorPositionField();
			cThostFtdcQryInvestorPositionField.setBrokerID(settings.getBrokerId());
			cThostFtdcQryInvestorPositionField.setInvestorID(settings.getUserId());
			cThostFtdcTraderApi.ReqQryInvestorPosition(cThostFtdcQryInvestorPositionField, reqId.incrementAndGet());
		} catch (Throwable t) {
			logger.error("{}交易接口查询持仓异常", logInfo, t);
		}

	}

	public String submitOrder(SubmitOrderReqField submitOrderReq) {
		if (cThostFtdcTraderApi == null) {
			logger.warn("{}交易接口尚未初始化,无法发单", logInfo);
			return null;
		}

		if (!loginStatus) {
			logger.warn("{}交易接口尚未登录,无法发单", logInfo);
			return null;
		}

		CThostFtdcInputOrderField cThostFtdcInputOrderField = new CThostFtdcInputOrderField();
		cThostFtdcInputOrderField.setInstrumentID(submitOrderReq.getContract().getSymbol());
		cThostFtdcInputOrderField.setLimitPrice(submitOrderReq.getPrice());
		cThostFtdcInputOrderField.setVolumeTotalOriginal(submitOrderReq.getVolume());
		cThostFtdcInputOrderField.setOrderPriceType(CtpConstant.orderPriceTypeMap.getOrDefault(submitOrderReq.getOrderPriceType(), Character.valueOf('\0')));
		cThostFtdcInputOrderField.setDirection(CtpConstant.directionMap.getOrDefault(submitOrderReq.getDirection(), Character.valueOf('\0')));
		cThostFtdcInputOrderField.setCombOffsetFlag(String.valueOf(CtpConstant.offsetFlagMap.getOrDefault(submitOrderReq.getOffsetFlag(), Character.valueOf('\0'))));
		cThostFtdcInputOrderField.setInvestorID(settings.getUserId());
		cThostFtdcInputOrderField.setUserID(settings.getUserId());
		cThostFtdcInputOrderField.setBrokerID(settings.getBrokerId());
		cThostFtdcInputOrderField.setExchangeID(CtpConstant.exchangeMap.getOrDefault(submitOrderReq.getContract().getExchange(), ""));
		cThostFtdcInputOrderField.setCombHedgeFlag(CtpConstant.hedgeFlagMap.get(submitOrderReq.getHedgeFlag()));
		cThostFtdcInputOrderField.setContingentCondition(CtpConstant.contingentConditionMap.get(submitOrderReq.getContingentCondition()));
		cThostFtdcInputOrderField.setForceCloseReason(CtpConstant.forceCloseReasonMap.get(submitOrderReq.getForceCloseReason()));
		cThostFtdcInputOrderField.setIsAutoSuspend(submitOrderReq.getAutoSuspend());
		cThostFtdcInputOrderField.setIsSwapOrder(submitOrderReq.getSwapOrder());
		cThostFtdcInputOrderField.setMinVolume(submitOrderReq.getMinVolume());
		cThostFtdcInputOrderField.setTimeCondition(CtpConstant.timeConditionMap.getOrDefault(submitOrderReq.getTimeCondition(), Character.valueOf('\0')));
		cThostFtdcInputOrderField.setVolumeCondition(CtpConstant.volumeConditionMap.getOrDefault(submitOrderReq.getVolumeCondition(), Character.valueOf('\0')));
		cThostFtdcInputOrderField.setStopPrice(submitOrderReq.getStopPrice());

		// 部分多线程场景下,如果不加锁,可能会导致自增乱序,因此导致发单失败
		submitOrderLock.lock();
		try {

			int orderRef = ++this.orderRef;

			String adapterOrderId = this.frontId + "_" + this.sessionId + "_" + orderRef;
			String orderId = gatewayId + "@" + adapterOrderId;

			if (StringUtils.isNotBlank(submitOrderReq.getOriginOrderId())) {
				orderIdToOriginalOrderIdMap.put(orderId, submitOrderReq.getOriginOrderId());
				originalOrderIdToOrderIdMap.put(submitOrderReq.getOriginOrderId(), orderId);
			}

			orderIdToSubmitOrderReqMap.put(orderId, submitOrderReq);
			orderIdToAdapterOrderIdMap.put(orderId, adapterOrderId);
			orderIdToOrderRefMap.put(orderId, orderRef + "");

			cThostFtdcInputOrderField.setOrderRef(orderRef + "");

			logger.info("{}交易接口发单记录->{\n" //
					+ "InstrumentID:{},\n" //
					+ "LimitPrice:{},\n" //
					+ "VolumeTotalOriginal:{},\n" //
					+ "OrderPriceType:{},\n" //
					+ "Direction:{},\n" //
					+ "CombOffsetFlag:{},\n" //
					+ "OrderRef:{},\n" //
					+ "InvestorID:{},\n" //
					+ "UserID:{},\n" //
					+ "BrokerID:{},\n" //
					+ "ExchangeID:{},\n" //
					+ "CombHedgeFlag:{},\n" //
					+ "ContingentCondition:{},\n" //
					+ "ForceCloseReason:{},\n" //
					+ "IsAutoSuspend:{},\n" //
					+ "IsSwapOrder:{},\n" //
					+ "MinVolume:{},\n" //
					+ "TimeCondition:{},\n" //
					+ "VolumeCondition:{},\n" //
					+ "StopPrice:{}}", //
					logInfo, //
					cThostFtdcInputOrderField.getInstrumentID(), //
					cThostFtdcInputOrderField.getLimitPrice(), //
					cThostFtdcInputOrderField.getVolumeTotalOriginal(), //
					cThostFtdcInputOrderField.getOrderPriceType(), //
					cThostFtdcInputOrderField.getDirection(), //
					cThostFtdcInputOrderField.getCombOffsetFlag(), //
					cThostFtdcInputOrderField.getOrderRef(), //
					cThostFtdcInputOrderField.getInvestorID(), //
					cThostFtdcInputOrderField.getUserID(), //
					cThostFtdcInputOrderField.getBrokerID(), //
					cThostFtdcInputOrderField.getExchangeID(), //
					cThostFtdcInputOrderField.getCombHedgeFlag(), //
					cThostFtdcInputOrderField.getContingentCondition(), //
					cThostFtdcInputOrderField.getForceCloseReason(), //
					cThostFtdcInputOrderField.getIsAutoSuspend(), //
					cThostFtdcInputOrderField.getIsSwapOrder(), //
					cThostFtdcInputOrderField.getMinVolume(), //
					cThostFtdcInputOrderField.getTimeCondition(), //
					cThostFtdcInputOrderField.getVolumeCondition(), //
					cThostFtdcInputOrderField.getStopPrice());
			cThostFtdcTraderApi.ReqOrderInsert(cThostFtdcInputOrderField, reqId.incrementAndGet());

			return orderId;
		} catch (Throwable t) {
			logger.error("{}交易接口发单错误", logInfo, t);
			return null;
		} finally {
			submitOrderLock.unlock();
		}

	}

	// 撤单
	public boolean cancelOrder(CancelOrderReqField cancelOrderReq) {

		if (cThostFtdcTraderApi == null) {
			logger.warn("{}交易接口尚未初始化,无法撤单", logInfo);
			return false;
		}

		if (!loginStatus) {
			logger.warn("{}交易接口尚未登录,无法撤单", logInfo);
			return false;
		}

		if (StringUtils.isBlank(cancelOrderReq.getOrderId()) && StringUtils.isBlank(cancelOrderReq.getOriginOrderId())) {
			logger.error("{}参数为空,无法撤单", logInfo);
			return false;
		}

		String orderId = cancelOrderReq.getOrderId();
		if (StringUtils.isBlank(orderId)) {
			orderId = originalOrderIdToOrderIdMap.get(cancelOrderReq.getOriginOrderId());
			if (StringUtils.isBlank(orderId)) {
				logger.error("{}交易接口未能找到有效定单号,无法撤单", logInfo);
				return false;
			}
		}

		try {
			CThostFtdcInputOrderActionField cThostFtdcInputOrderActionField = new CThostFtdcInputOrderActionField();
			if (orderIdToSubmitOrderReqMap.containsKey(orderId)) {

				cThostFtdcInputOrderActionField.setInstrumentID(orderIdToSubmitOrderReqMap.get(orderId).getContract().getSymbol());
				cThostFtdcInputOrderActionField.setExchangeID(CtpConstant.exchangeMap.getOrDefault(orderIdToSubmitOrderReqMap.get(orderId).getContract().getExchange(), ""));
				cThostFtdcInputOrderActionField.setOrderRef(orderIdToOrderRefMap.get(orderId));
				cThostFtdcInputOrderActionField.setFrontID(frontId);
				cThostFtdcInputOrderActionField.setSessionID(sessionId);

				cThostFtdcInputOrderActionField.setActionFlag(jctpv6v5v1cpx64apiConstants.THOST_FTDC_AF_Delete);
				cThostFtdcInputOrderActionField.setBrokerID(settings.getBrokerId());
				cThostFtdcInputOrderActionField.setInvestorID(settings.getUserId());
				cThostFtdcInputOrderActionField.setUserID(settings.getUserId());
				cThostFtdcInputOrderActionField.setExchangeID(CtpConstant.exchangeMap.getOrDefault(orderIdToSubmitOrderReqMap.get(orderId).getContract().getExchange(), ""));
				cThostFtdcTraderApi.ReqOrderAction(cThostFtdcInputOrderActionField, reqId.incrementAndGet());
				return true;

			} else if (orderIdToOrderMap.containsKey(orderId)) {
				cThostFtdcInputOrderActionField.setInstrumentID(orderIdToOrderMap.get(orderId).getContract().getSymbol());
				cThostFtdcInputOrderActionField.setExchangeID(CtpConstant.exchangeMap.getOrDefault(orderIdToOrderMap.get(orderId).getContract().getExchange(), ""));
				cThostFtdcInputOrderActionField.setOrderRef(orderIdToOrderRefMap.get(orderId));
				cThostFtdcInputOrderActionField.setFrontID(orderIdToOrderMap.get(orderId).getFrontId());
				cThostFtdcInputOrderActionField.setSessionID(orderIdToOrderMap.get(orderId).getSessionId());

				cThostFtdcInputOrderActionField.setActionFlag(jctpv6v5v1cpx64apiConstants.THOST_FTDC_AF_Delete);
				cThostFtdcInputOrderActionField.setBrokerID(settings.getBrokerId());
				cThostFtdcInputOrderActionField.setInvestorID(settings.getUserId());
				cThostFtdcInputOrderActionField.setUserID(settings.getUserId());
				cThostFtdcInputOrderActionField.setExchangeID(CtpConstant.exchangeMap.getOrDefault(orderIdToOrderMap.get(orderId).getContract().getExchange(), ""));
				cThostFtdcTraderApi.ReqOrderAction(cThostFtdcInputOrderActionField, reqId.incrementAndGet());
				return true;
			} else {
				logger.error("{}无法找到定单请求或者回报,无法撤单", logInfo);
				return false;
			}
		} catch (Throwable t) {
			logger.error("{}撤单异常", logInfo, t);
			return false;
		}

	}

	private void reqAuth() {
		if (loginFailed) {
			logger.warn("{}交易接口登录曾发生错误,不再登录,以防被锁", logInfo);
			return;
		}

		if (cThostFtdcTraderApi == null) {
			logger.warn("{}发起客户端验证请求错误,交易接口实例不存在", logInfo);
			return;
		}

		if (StringUtils.isEmpty(settings.getBrokerId())) {
			logger.error("{}BrokerID不允许为空", logInfo);
			return;
		}

		if (StringUtils.isEmpty(settings.getUserId())) {
			logger.error("{}UserId不允许为空", logInfo);
			return;
		}

		if (StringUtils.isEmpty(settings.getPassword())) {
			logger.error("{}Password不允许为空", logInfo);
			return;
		}

		if (StringUtils.isEmpty(settings.getAppId())) {
			logger.error("{}AppId不允许为空", logInfo);
			return;
		}
		if (StringUtils.isEmpty(settings.getAuthCode())) {
			logger.error("{}AuthCode不允许为空", logInfo);
			return;
		}

		try {
			gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.LOGGING_IN, gatewayId);
			CThostFtdcReqAuthenticateField authenticateField = new CThostFtdcReqAuthenticateField();
			authenticateField.setAppID(settings.getAppId());
			authenticateField.setAuthCode(settings.getAuthCode());
			authenticateField.setBrokerID(settings.getBrokerId());
			authenticateField.setUserProductInfo(settings.getAppId());
			authenticateField.setUserID(settings.getUserId());
			cThostFtdcTraderApi.ReqAuthenticate(authenticateField, reqId.incrementAndGet());
		} catch (Throwable t) {
			logger.error("{}发起客户端验证异常", logInfo, t);
			gatewayAdapter.disconnect();
		}

	}

	// 前置机联机回报
	public void OnFrontConnected() {
		try {
			logger.info("{}交易接口前置机已连接", logInfo);
			// 修改前置机连接状态
			gatewayAdapter.setConnectionState(ConnectionState.CONNECTED);
			
			reqAuth();
			
		} catch (Throwable t) {
			logger.error("{}OnFrontConnected Exception", logInfo, t);
		}
	}

	// 前置机断开回报
	public void OnFrontDisconnected(int nReason) {
		try {
			logger.warn("{}交易接口前置机已断开, 原因:{}", logInfo, nReason);
			gatewayAdapter.disconnect();
			gatewayAdapter.setConnectionState(ConnectionState.DISCONNECTED);
			gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.LOGGED_OUT, gatewayId);
			
		} catch (Throwable t) {
			logger.error("{}OnFrontDisconnected Exception", logInfo, t);
		}
	}

	// 登录回报
	public void OnRspUserLogin(CThostFtdcRspUserLoginField pRspUserLogin, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if (pRspInfo.getErrorID() == 0) {
				logger.info("{}交易接口登录成功 TradingDay:{},SessionID:{},BrokerID:{},UserID:{}", logInfo, pRspUserLogin.getTradingDay(), pRspUserLogin.getSessionID(), pRspUserLogin.getBrokerID(),
						pRspUserLogin.getUserID());
				sessionId = pRspUserLogin.getSessionID();
				frontId = pRspUserLogin.getFrontID();
				// 修改登录状态为true
				loginStatus = true;
				tradingDay = pRspUserLogin.getTradingDay();
				logger.info("{}交易接口获取到的交易日为{}", logInfo, tradingDay);

				// 确认结算单
				CThostFtdcSettlementInfoConfirmField settlementInfoConfirmField = new CThostFtdcSettlementInfoConfirmField();
				settlementInfoConfirmField.setBrokerID(settings.getBrokerId());
				settlementInfoConfirmField.setInvestorID(settings.getUserId());
				cThostFtdcTraderApi.ReqSettlementInfoConfirm(settlementInfoConfirmField, reqId.incrementAndGet());

				// 不合法的登录
				if (pRspInfo.getErrorID() == 3) {
					gatewayAdapter.setAuthErrorFlag(true);
					return;
				}

				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.LOGGED_IN, gatewayId);
			} else {
				logger.error("{}交易接口登录回报错误 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
				loginFailed = true;
			}
		} catch (Throwable t) {
			logger.error("{}交易接口处理登录回报异常", logInfo, t);
			loginFailed = true;
		}

	}

	// 心跳警告
	public void OnHeartBeatWarning(int nTimeLapse) {
		logger.warn("{}交易接口心跳警告, Time Lapse:{}", logInfo, nTimeLapse);
	}

	// 登出回报
	public void OnRspUserLogout(CThostFtdcUserLogoutField pUserLogout, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if (pRspInfo.getErrorID() != 0) {
				logger.error("{}OnRspUserLogout!错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
			} else {
				logger.info("{}OnRspUserLogout!BrokerID:{},UserId:{}", logInfo, pUserLogout.getBrokerID(), pUserLogout.getUserID());

			}
		} catch (Throwable t) {
			logger.error("{}交易接口处理登出回报错误", logInfo, t);
		}

		loginStatus = false;
	}

	// 错误回报
	public void OnRspError(CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			logger.error("{}交易接口错误回报!错误ID:{},错误信息:{},请求ID:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg(), nRequestID);
			if (instrumentQueried) {
				if (pRspInfo.getErrorID() == 0) {
					NoticeField notice  = NoticeField.newBuilder()
						.setContent(logInfo + "交易接口错误回报:" + pRspInfo.getErrorMsg() + "，错误ID:" + pRspInfo.getErrorID())
						.setStatus(CommonStatusEnum.COMS_INFO)
						.setTimestamp(System.currentTimeMillis())
						.build();
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
				} else {
					NoticeField notice = NoticeField.newBuilder()
							.setContent(logInfo + "交易接口错误回报:" + pRspInfo.getErrorMsg() + "，错误ID:" + pRspInfo.getErrorID())
							.setStatus(CommonStatusEnum.COMS_ERROR)
							.setTimestamp(System.currentTimeMillis())
							.build();
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
				}
			}
			// CTP查询尚未就绪,断开
			if (pRspInfo.getErrorID() == 90) {
				gatewayAdapter.disconnect();
			}
		} catch (Throwable t) {
			logger.error("{}OnRspError Exception", logInfo, t);
		}
	}

	// 验证客户端回报
	public void OnRspAuthenticate(CThostFtdcRspAuthenticateField pRspAuthenticateField, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if (pRspInfo != null) {
				if (pRspInfo.getErrorID() == 0) {
					logger.info("{}{}", logInfo, "交易接口客户端验证成功");
					CThostFtdcReqUserLoginField reqUserLoginField = new CThostFtdcReqUserLoginField();
					reqUserLoginField.setBrokerID(settings.getBrokerId());
					reqUserLoginField.setUserID(settings.getUserId());
					reqUserLoginField.setPassword(settings.getPassword());
					cThostFtdcTraderApi.ReqUserLogin(reqUserLoginField, reqId.incrementAndGet());
					
				} else {

					logger.error("{}交易接口客户端验证失败 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
					loginFailed = true;

					// 客户端验证失败
					if (pRspInfo.getErrorID() == 63) {
						gatewayAdapter.setAuthErrorFlag(true);
					}
				}
			} else {
				loginFailed = true;
				logger.error("{}处理交易接口客户端验证回报错误,回报信息为空", logInfo);
			}
		} catch (Throwable t) {
			loginFailed = true;
			logger.error("{}处理交易接口客户端验证回报异常", logInfo, t);
		}
	}

	public void OnRspUserPasswordUpdate(CThostFtdcUserPasswordUpdateField pUserPasswordUpdate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspTradingAccountPasswordUpdate(CThostFtdcTradingAccountPasswordUpdateField pTradingAccountPasswordUpdate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	// 发单错误
	public void OnRspOrderInsert(CThostFtdcInputOrderField pInputOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if (pInputOrder != null) {

				String symbol = pInputOrder.getInstrumentID();

				// 无法获取账户信息,使用userId作为账户ID
				String accountCode = settings.getUserId();
				// 无法获取币种信息使用特定值CNY
				String accountId = accountCode + "@" + gatewayId;

				int frontId = this.frontId;
				int sessionId = this.sessionId;
				String orderRef = StringUtils.trim(pInputOrder.getOrderRef());

				String adapterOrderId = frontId + "_" + sessionId + "_" + orderRef;
				String orderId = gatewayId + "@" + adapterOrderId;

				DirectionEnum direction = CtpConstant.directionMapReverse.getOrDefault(pInputOrder.getDirection(), DirectionEnum.D_Unknown);
				OffsetFlagEnum offsetflag = CtpConstant.offsetMapReverse.getOrDefault(pInputOrder.getCombOffsetFlag().toCharArray()[0], OffsetFlagEnum.OF_Unknown);

				double price = pInputOrder.getLimitPrice();
				int totalVolume = pInputOrder.getVolumeTotalOriginal();
				int tradedVolume = 0;

				OrderStatusEnum orderStatus = OrderStatusEnum.OS_Rejected;

				HedgeFlagEnum hedgeFlag = CtpConstant.hedgeFlagMapReverse.getOrDefault(pInputOrder.getCombHedgeFlag(), HedgeFlagEnum.HF_Unknown);
				ContingentConditionEnum contingentCondition = CtpConstant.contingentConditionMapReverse.getOrDefault(pInputOrder.getContingentCondition(), ContingentConditionEnum.CC_Unknown);
				ForceCloseReasonEnum forceCloseReason = CtpConstant.forceCloseReasonMapReverse.getOrDefault(pInputOrder.getForceCloseReason(), ForceCloseReasonEnum.FCR_Unknown);
				TimeConditionEnum timeCondition = CtpConstant.timeConditionMapReverse.getOrDefault(pInputOrder.getTimeCondition(), TimeConditionEnum.TC_Unknown);
				String gtdDate = pInputOrder.getGTDDate();
				int autoSuspend = pInputOrder.getIsAutoSuspend();
				int userForceClose = pInputOrder.getUserForceClose();
				int swapOrder = pInputOrder.getIsSwapOrder();
				VolumeConditionEnum volumeCondition = CtpConstant.volumeConditionMapReverse.getOrDefault(pInputOrder.getVolumeCondition(), VolumeConditionEnum.VC_Unknown);
				OrderPriceTypeEnum orderPriceType = CtpConstant.orderPriceTypeMapReverse.getOrDefault(pInputOrder.getOrderPriceType(), OrderPriceTypeEnum.OPT_Unknown);

				int minVolume = pInputOrder.getMinVolume();
				double stopPrice = pInputOrder.getStopPrice();

				String originalOrderId = orderIdToOriginalOrderIdMap.getOrDefault(orderId, "");

				OrderField.Builder orderBuilder = OrderField.newBuilder();
				orderBuilder.setAccountId(accountId);
				orderBuilder.setOriginOrderId(originalOrderId);
				orderBuilder.setOrderId(orderId);
				orderBuilder.setAdapterOrderId(adapterOrderId);
				orderBuilder.setDirection(direction);
				orderBuilder.setOffsetFlag(offsetflag);
				orderBuilder.setPrice(price);
				orderBuilder.setTotalVolume(totalVolume);
				orderBuilder.setTradedVolume(tradedVolume);
				orderBuilder.setOrderStatus(orderStatus);
				orderBuilder.setTradingDay(tradingDay);
				orderBuilder.setFrontId(frontId);
				orderBuilder.setSessionId(sessionId);
				orderBuilder.setGatewayId(gatewayId);
				orderBuilder.setHedgeFlag(hedgeFlag);
				orderBuilder.setContingentCondition(contingentCondition);
				orderBuilder.setForceCloseReason(forceCloseReason);
				orderBuilder.setTimeCondition(timeCondition);
				orderBuilder.setGtdDate(gtdDate);
				orderBuilder.setAutoSuspend(autoSuspend);
				orderBuilder.setVolumeCondition(volumeCondition);
				orderBuilder.setMinVolume(minVolume);
				orderBuilder.setStopPrice(stopPrice);
				orderBuilder.setUserForceClose(userForceClose);
				orderBuilder.setSwapOrder(swapOrder);
				orderBuilder.setOrderPriceType(orderPriceType);

				if (pRspInfo != null && pRspInfo.getErrorMsg() != null) {
					orderBuilder.setStatusMsg(pRspInfo.getErrorMsg());
				}

				if (instrumentQueried) {
					ContractField contract = gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol).contractField();
					price = (int)(price / contract.getPriceTick()) * contract.getPriceTick();
					orderBuilder.setContract(contract);
					orderBuilder.setPrice(price);	// 优化价格精度
					OrderField order = orderBuilder.build();
					orderIdToOrderMap.put(order.getOrderId(), order);
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.ORDER, order);
				} else {
					ContractField.Builder contractBuilder = ContractField.newBuilder();
					contractBuilder.setSymbol(symbol);
					orderBuilder.setContract(contractBuilder.build());

					orderBuilderCacheList.add(orderBuilder);
				}
			} else {
				logger.error("{}处理交易接口发单错误回报(OnRspOrderInsert)错误,空数据", logInfo);
			}

			if (pRspInfo != null) {
				logger.error("{}交易接口发单错误回报(OnRspOrderInsert) 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
				if (instrumentQueried) {
					NoticeField notice  = NoticeField.newBuilder()
						.setContent(logInfo + "交易接口发单错误回报，错误ID:" + pRspInfo.getErrorID() + "，错误信息:" + pRspInfo.getErrorMsg())
						.setStatus(CommonStatusEnum.COMS_ERROR)
						.setTimestamp(System.currentTimeMillis())
						.build();
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
				}
			} else {
				logger.error("{}处理交易接口发单错误回报(OnRspOrderInsert)错误,回报信息为空", logInfo);
			}

		} catch (Throwable t) {
			logger.error("{}处理交易接口发单错误回报(OnRspOrderInsert)异常", logInfo, t);
		}

	}

	public void OnRspParkedOrderInsert(CThostFtdcParkedOrderField pParkedOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspParkedOrderAction(CThostFtdcParkedOrderActionField pParkedOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	// 撤单错误回报
	public void OnRspOrderAction(CThostFtdcInputOrderActionField pInputOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		if (pRspInfo != null) {
			logger.error("{}交易接口撤单错误回报(OnRspOrderAction) 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
			if (instrumentQueried) {
				NoticeField notice  = NoticeField.newBuilder()
					.setContent(logInfo + "交易接口撤单错误回报，错误ID:" + pRspInfo.getErrorID() + "，错误信息:" + pRspInfo.getErrorMsg())
					.setStatus(CommonStatusEnum.COMS_ERROR)
					.setTimestamp(System.currentTimeMillis())
					.build();
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
			}
		} else {
			logger.error("{}处理交易接口撤单错误回报(OnRspOrderAction)错误,无有效信息", logInfo);
		}
	}

	// 确认结算信息回报
	public void OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField pSettlementInfoConfirm, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if(pRspInfo == null) {
				logger.warn("交易结算信息为空");
			} else if (pRspInfo.getErrorID() == 0) {
				logger.info("{}交易接口结算信息确认完成", logInfo);
			} else {
				logger.error("{}交易接口结算信息确认出错 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
				gatewayAdapter.disconnect();
				return;
			}

			// 防止被限流
			Thread.sleep(1000);

			logger.info("{}交易接口开始查询投资者信息", logInfo);
			CThostFtdcQryInvestorField pQryInvestor = new CThostFtdcQryInvestorField();
			pQryInvestor.setInvestorID(settings.getUserId());
			pQryInvestor.setBrokerID(settings.getBrokerId());
			cThostFtdcTraderApi.ReqQryInvestor(pQryInvestor, reqId.addAndGet(1));
		} catch (Throwable t) {
			logger.error("{}处理结算单确认回报错误", logInfo, t);
//			gatewayAdapter.disconnect();
		}
	}

	public void OnRspRemoveParkedOrder(CThostFtdcRemoveParkedOrderField pRemoveParkedOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspRemoveParkedOrderAction(CThostFtdcRemoveParkedOrderActionField pRemoveParkedOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspExecOrderInsert(CThostFtdcInputExecOrderField pInputExecOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspExecOrderAction(CThostFtdcInputExecOrderActionField pInputExecOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspForQuoteInsert(CThostFtdcInputForQuoteField pInputForQuote, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQuoteInsert(CThostFtdcInputQuoteField pInputQuote, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQuoteAction(CThostFtdcInputQuoteActionField pInputQuoteAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspBatchOrderAction(CThostFtdcInputBatchOrderActionField pInputBatchOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspOptionSelfCloseInsert(CThostFtdcInputOptionSelfCloseField pInputOptionSelfClose, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspOptionSelfCloseAction(CThostFtdcInputOptionSelfCloseActionField pInputOptionSelfCloseAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspCombActionInsert(CThostFtdcInputCombActionField pInputCombAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryOrder(CThostFtdcOrderField pOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryTrade(CThostFtdcTradeField pTrade, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	// 持仓查询回报
	public void OnRspQryInvestorPosition(CThostFtdcInvestorPositionField pInvestorPosition, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {

		try {
			if (pInvestorPosition == null || StringUtils.isEmpty(pInvestorPosition.getInstrumentID())) {
				return;
			}
			String symbol = pInvestorPosition.getInstrumentID();

			if (!instrumentQueried) {
				logger.debug("{}尚未获取到合约信息,暂时不处理持仓数据,代码{}", logInfo, symbol);
				return;
			}

			Contract contract = gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol);

			String uniqueSymbol = symbol + "@" + contract.exchange().getValueDescriptor().getName() + "@" + contract.productClass().getValueDescriptor().getName();

			// 无法获取账户信息,使用userId作为账户ID
			String accountCode = settings.getUserId();
			// 无法获取币种信息使用特定值
			String accountId = accountCode + "@" + gatewayId;

			PositionDirectionEnum direction = CtpConstant.posiDirectionMapReverse.getOrDefault(pInvestorPosition.getPosiDirection(), PositionDirectionEnum.PD_Unknown);
			HedgeFlagEnum hedgeFlag = CtpConstant.hedgeFlagMapReverse.get(String.valueOf(pInvestorPosition.getHedgeFlag()));
			// 获取持仓缓存
			String positionId = uniqueSymbol + "@" + direction.getValueDescriptor().getName() + "@" + hedgeFlag.getValueDescriptor().getName() + "@" + accountId;

			PositionField.Builder positionBuilder;
			if (positionBuilderMap.containsKey(positionId)) {
				positionBuilder = positionBuilderMap.get(positionId);
			} else {
				positionBuilder = PositionField.newBuilder();
				positionBuilderMap.put(positionId, positionBuilder);
				positionBuilder.setContract(contract.contractField());
				positionBuilder.setPositionDirection(CtpConstant.posiDirectionMapReverse.getOrDefault(pInvestorPosition.getPosiDirection(), PositionDirectionEnum.PD_Unknown));
				positionBuilder.setPositionId(positionId);

				positionBuilder.setAccountId(accountId);
				positionBuilder.setGatewayId(gatewayId);
				positionBuilder.setHedgeFlag(hedgeFlag);

			}

			positionBuilder.setUseMargin(positionBuilder.getUseMargin() + pInvestorPosition.getUseMargin());
			positionBuilder.setExchangeMargin(positionBuilder.getExchangeMargin() + pInvestorPosition.getExchangeMargin());

			positionBuilder.setPosition(positionBuilder.getPosition() + pInvestorPosition.getPosition());

			if (positionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Long) {
				positionBuilder.setFrozen(pInvestorPosition.getShortFrozen());
			} else {
				positionBuilder.setFrozen(pInvestorPosition.getLongFrozen());
			}

			if (ExchangeEnum.INE == positionBuilder.getContract().getExchange() || ExchangeEnum.SHFE == positionBuilder.getContract().getExchange()) {
				// 针对上期所、上期能源持仓的今昨分条返回（有昨仓、无今仓）,读取昨仓数据
				if (pInvestorPosition.getYdPosition() > 0 && pInvestorPosition.getTodayPosition() == 0) {

					positionBuilder.setYdPosition(positionBuilder.getYdPosition() + pInvestorPosition.getPosition());

					if (positionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Long) {
						positionBuilder.setYdFrozen(positionBuilder.getYdFrozen() + pInvestorPosition.getShortFrozen());
					} else {
						positionBuilder.setYdFrozen(positionBuilder.getYdFrozen() + pInvestorPosition.getLongFrozen());
					}
				} else {
					positionBuilder.setTdPosition(positionBuilder.getTdPosition() + pInvestorPosition.getPosition());

					if (positionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Long) {
						positionBuilder.setTdFrozen(positionBuilder.getTdFrozen() + pInvestorPosition.getShortFrozen());
					} else {
						positionBuilder.setTdFrozen(positionBuilder.getTdFrozen() + pInvestorPosition.getLongFrozen());
					}
				}
			} else {
				positionBuilder.setTdPosition(positionBuilder.getTdPosition() + pInvestorPosition.getTodayPosition());
				positionBuilder.setYdPosition(positionBuilder.getPosition() - positionBuilder.getTdPosition());

				// 中金所优先平今
				if (ExchangeEnum.CFFEX == positionBuilder.getContract().getExchange()) {
					if (positionBuilder.getTdPosition() > 0) {
						if (positionBuilder.getTdPosition() >= positionBuilder.getFrozen()) {
							positionBuilder.setTdFrozen(positionBuilder.getFrozen());
						} else {
							positionBuilder.setTdFrozen(positionBuilder.getTdPosition());
							positionBuilder.setYdFrozen(positionBuilder.getFrozen() - positionBuilder.getTdPosition());
						}
					} else {
						positionBuilder.setYdFrozen(positionBuilder.getFrozen());
					}
				} else {
					// 除了上面几个交易所之外的交易所，优先平昨
					if (positionBuilder.getYdPosition() > 0) {
						if (positionBuilder.getYdPosition() >= positionBuilder.getFrozen()) {
							positionBuilder.setYdFrozen(positionBuilder.getFrozen());
						} else {
							positionBuilder.setYdFrozen(positionBuilder.getYdPosition());
							positionBuilder.setTdFrozen(positionBuilder.getFrozen() - positionBuilder.getYdPosition());
						}
					} else {
						positionBuilder.setTdFrozen(positionBuilder.getFrozen());
					}
				}

			}

			// 计算成本
			double cost = positionBuilder.getPrice() * positionBuilder.getPosition() * positionBuilder.getContract().getMultiplier();
			double openCost = positionBuilder.getOpenPrice() * positionBuilder.getPosition() * positionBuilder.getContract().getMultiplier();

			// 汇总总仓
			positionBuilder.setPositionProfit(positionBuilder.getPositionProfit() + pInvestorPosition.getPositionProfit());

			// 计算持仓均价
			if (positionBuilder.getPosition() != 0) {
				positionBuilder.setPrice((cost + pInvestorPosition.getPositionCost()) / (positionBuilder.getPosition() * positionBuilder.getContract().getMultiplier()));
				positionBuilder.setOpenPrice((openCost + pInvestorPosition.getOpenCost()) / (positionBuilder.getPosition() * positionBuilder.getContract().getMultiplier()));
			}

			// 回报结束
			if (bIsLast) {
				for (PositionField.Builder tmpPositionBuilder : positionBuilderMap.values()) {

					if (tmpPositionBuilder.getPosition() != 0) {

						tmpPositionBuilder.setPriceDiff(tmpPositionBuilder.getPositionProfit() / tmpPositionBuilder.getContract().getMultiplier() / tmpPositionBuilder.getPosition());

						if (tmpPositionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Long
								|| (tmpPositionBuilder.getPosition() > 0 && tmpPositionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Net)) {

							// 计算最新价格
							tmpPositionBuilder.setLastPrice(tmpPositionBuilder.getPrice() + tmpPositionBuilder.getPriceDiff());
							// 计算开仓价格差距
							tmpPositionBuilder.setOpenPriceDiff(tmpPositionBuilder.getLastPrice() - tmpPositionBuilder.getOpenPrice());
							// 计算开仓盈亏
							tmpPositionBuilder.setOpenPositionProfit(tmpPositionBuilder.getOpenPriceDiff() * tmpPositionBuilder.getPosition() * tmpPositionBuilder.getContract().getMultiplier());

						} else if (tmpPositionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Short
								|| (tmpPositionBuilder.getPosition() < 0 && tmpPositionBuilder.getPositionDirection() == PositionDirectionEnum.PD_Net)) {

							// 计算最新价格
							tmpPositionBuilder.setLastPrice(tmpPositionBuilder.getPrice() - tmpPositionBuilder.getPriceDiff());
							// 计算开仓价格差距
							tmpPositionBuilder.setOpenPriceDiff(tmpPositionBuilder.getOpenPrice() - tmpPositionBuilder.getLastPrice());
							// 计算开仓盈亏
							tmpPositionBuilder.setOpenPositionProfit(tmpPositionBuilder.getOpenPriceDiff() * tmpPositionBuilder.getPosition() * tmpPositionBuilder.getContract().getMultiplier());

						} else {
							logger.error("{}计算持仓时发现未处理方向，持仓详情{}", logInfo, tmpPositionBuilder.toString());
						}

						// 计算保最新合约价值
						tmpPositionBuilder.setContractValue(tmpPositionBuilder.getLastPrice() * tmpPositionBuilder.getContract().getMultiplier() * tmpPositionBuilder.getPosition());

						if (tmpPositionBuilder.getUseMargin() != 0) {
							tmpPositionBuilder.setPositionProfitRatio(tmpPositionBuilder.getPositionProfit() / tmpPositionBuilder.getUseMargin());
							tmpPositionBuilder.setOpenPositionProfitRatio(tmpPositionBuilder.getOpenPositionProfit() / tmpPositionBuilder.getUseMargin());

						}
					}
					// 发送持仓事件
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.POSITION, tmpPositionBuilder.build());
				}
				// 清空缓存
				positionBuilderMap = new HashMap<>();
			}

		} catch (Throwable t) {
			logger.error("{}处理查询持仓回报异常", logInfo, t);
			gatewayAdapter.disconnect();
		}
	}

	// 账户查询回报
	public void OnRspQryTradingAccount(CThostFtdcTradingAccountField pTradingAccount, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {

		try {
			String accountCode = pTradingAccount.getAccountID();
			String currency = pTradingAccount.getCurrencyID();

			if (StringUtils.isBlank(currency)) {
				currency = "CNY";
			}

			String accountId = accountCode + "@" + gatewayId;

			AccountField.Builder accountBuilder = AccountField.newBuilder();
			accountBuilder.setCode(accountCode);
			accountBuilder.setCurrency(CurrencyEnum.valueOf(currency));
			accountBuilder.setAvailable(pTradingAccount.getAvailable());
			accountBuilder.setCloseProfit(pTradingAccount.getCloseProfit());
			accountBuilder.setCommission(pTradingAccount.getCommission());
			accountBuilder.setGatewayId(gatewayId);
			accountBuilder.setMargin(pTradingAccount.getCurrMargin());
			accountBuilder.setPositionProfit(pTradingAccount.getPositionProfit());
			accountBuilder.setPreBalance(pTradingAccount.getPreBalance());
			accountBuilder.setAccountId(accountId);
			accountBuilder.setDeposit(pTradingAccount.getDeposit());
			accountBuilder.setWithdraw(pTradingAccount.getWithdraw());
			accountBuilder.setHolder(investorName);

			accountBuilder.setBalance(pTradingAccount.getBalance());

			gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.ACCOUNT, accountBuilder.build());
		} catch (Throwable t) {
			logger.error("{}处理查询账户回报异常", logInfo, t);
			gatewayAdapter.disconnect();
		}

	}

	public void OnRspQryInvestor(CThostFtdcInvestorField pInvestor, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			if (pRspInfo != null && pRspInfo.getErrorID() != 0) {
				logger.error("{}查询投资者信息失败 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
				gatewayAdapter.disconnect();
			} else {
				if (pInvestor != null) {
					investorName = pInvestor.getInvestorName();
					logger.info("{}交易接口获取到的投资者名为:{}", logInfo, investorName);
				} else {
					logger.error("{}交易接口未能获取到投资者名", logInfo);
				}
			}

			if (bIsLast) {
				if (StringUtils.isBlank(investorName)) {
					logger.warn("{}交易接口未能获取到投资者名", logInfo);
					NoticeField notice = NoticeField.newBuilder()
							.setContent(logInfo + "交易接口投资者名为空")
							.setStatus(CommonStatusEnum.COMS_WARN)
							.setTimestamp(System.currentTimeMillis())
							.build();
					gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
				}
				investorNameQueried = true;
				// 防止被限流
				Thread.sleep(1000);
				// 查询所有合约
				logger.info("{}交易接口开始查询合约信息", logInfo);
				CThostFtdcQryInstrumentField cThostFtdcQryInstrumentField = new CThostFtdcQryInstrumentField();
				cThostFtdcTraderApi.ReqQryInstrument(cThostFtdcQryInstrumentField, reqId.incrementAndGet());
			}
		} catch (Throwable t) {
			logger.error("{}处理查询投资者回报异常", logInfo, t);
			gatewayAdapter.disconnect();
		}
	}

	public void OnRspQryTradingCode(CThostFtdcTradingCodeField pTradingCode, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField pInstrumentMarginRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField pInstrumentCommissionRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryExchange(CThostFtdcExchangeField pExchange, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryProduct(CThostFtdcProductField pProduct, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	// 合约查询回报
	public void OnRspQryInstrument(CThostFtdcInstrumentField pInstrument, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
		try {
			String symbol = pInstrument.getInstrumentID();
			String name = Optional.ofNullable(CtpContractNameResolver.getCNSymbolName(symbol)).orElse(pInstrument.getInstrumentName());
			ExchangeEnum exchange = CtpConstant.exchangeMapReverse.getOrDefault(pInstrument.getExchangeID(), ExchangeEnum.UnknownExchange);
			ProductClassEnum productClass = CtpConstant.productTypeMapReverse.getOrDefault(pInstrument.getProductClass(), ProductClassEnum.UnknownProductClass);
			String unifiedSymbol = String.format("%s@%s@%s", symbol, exchange, productClass);
			String contractId = String.format("%s@%s", unifiedSymbol, MKT_GATEWAY_ID);
			
			CtpContract contract = CtpContract.builder()
					.gatewayId(MKT_GATEWAY_ID)
					.symbol(symbol)
					.name(name)
					.fullName(pInstrument.getInstrumentName())
					.thirdPartyId(symbol + "@" + MKT_GATEWAY_ID)
					.exchange(exchange)
					.productClass(productClass)
					.unifiedSymbol(unifiedSymbol)
					.contractId(contractId)
					.multiplier(Math.max(1, pInstrument.getVolumeMultiple()))
					.priceTick(pInstrument.getPriceTick())
					.currency(CurrencyEnum.CNY)
					.lastTradeDateOrContractMonth(pInstrument.getExpireDate())
					.strikePrice(pInstrument.getStrikePrice())
					.optionsType(CtpConstant.optionTypeMapReverse.getOrDefault(pInstrument.getOptionsType(), OptionsTypeEnum.O_Unknown))
					.underlyingSymbol(Optional.ofNullable(pInstrument.getUnderlyingInstrID()).orElse(""))
					.underlyingMultiplier(pInstrument.getUnderlyingMultiple())
					.maxLimitOrderVolume(pInstrument.getMaxLimitOrderVolume())
					.minLimitOrderVolume(pInstrument.getMinLimitOrderVolume())
					.maxMarketOrderVolume(pInstrument.getMaxMarketOrderVolume())
					.minMarketOrderVolume(pInstrument.getMinMarketOrderVolume())
					.maxMarginSideAlgorithm(pInstrument.getMaxMarginSideAlgorithm() == '1')
					.longMarginRatio(pInstrument.getLongMarginRatio())
					.shortMarginRatio(pInstrument.getShortMarginRatio())
					.build();
			
			gatewayAdapter.mktCenter.addInstrument(contract);
			
			if (bIsLast) {
				logger.info("{}交易接口合约信息获取完成!共计{}条", logInfo, gatewayAdapter.mktCenter.getContracts(MKT_GATEWAY_ID).size());
				
				instrumentQueried = true;
				this.startIntervalQuery();

				logger.info("{}交易接口开始推送缓存Order,共计{}条", logInfo, orderBuilderCacheList.size());
				for (OrderField.Builder orderBuilder : orderBuilderCacheList) {
					try {
						orderBuilder.setContract(gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol).contractField());
						OrderField order = orderBuilder.build();
						orderIdToOrderMap.put(order.getOrderId(), order);
						gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.ORDER, order);
					} catch(NoSuchElementException e) {
						logger.error("{}未能正确获取到合约信息，代码{}", logInfo, orderBuilder.getContract().getSymbol());
					}
				}
				orderBuilderCacheList.clear();

				logger.info("{}交易接口开始推送缓存Trade,共计{}条", logInfo, tradeBuilderCacheList.size());
				for (TradeField.Builder tradeBuilder : tradeBuilderCacheList) {
					try {
						tradeBuilder.setContract(gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol).contractField());
						gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.TRADE, tradeBuilder.build());
					} catch(NoSuchElementException e) {
						logger.error("{}未能正确获取到合约信息，代码{}", logInfo, tradeBuilder.getContract().getSymbol());
					}
				}
				tradeBuilderCacheList.clear();
				gatewayAdapter.mktCenter.loadContractGroup(ChannelType.CTP);
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.GATEWAY_READY, gatewayId);
			}
		} catch (Throwable t) {
			logger.error("{}OnRspQryInstrument Exception", logInfo, t);
		}

	}

	public void OnRspQryDepthMarketData(CThostFtdcDepthMarketDataField pDepthMarketData, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQrySettlementInfo(CThostFtdcSettlementInfoField pSettlementInfo, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryTransferBank(CThostFtdcTransferBankField pTransferBank, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField pInvestorPositionDetail, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryNotice(CThostFtdcNoticeField pNotice, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQrySettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField pSettlementInfoConfirm, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInvestorPositionCombineDetail(CThostFtdcInvestorPositionCombineDetailField pInvestorPositionCombineDetail, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryCFMMCTradingAccountKey(CThostFtdcCFMMCTradingAccountKeyField pCFMMCTradingAccountKey, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryEWarrantOffset(CThostFtdcEWarrantOffsetField pEWarrantOffset, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInvestorProductGroupMargin(CThostFtdcInvestorProductGroupMarginField pInvestorProductGroupMargin, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryExchangeMarginRate(CThostFtdcExchangeMarginRateField pExchangeMarginRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryExchangeMarginRateAdjust(CThostFtdcExchangeMarginRateAdjustField pExchangeMarginRateAdjust, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryExchangeRate(CThostFtdcExchangeRateField pExchangeRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQrySecAgentACIDMap(CThostFtdcSecAgentACIDMapField pSecAgentACIDMap, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryProductExchRate(CThostFtdcProductExchRateField pProductExchRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryProductGroup(CThostFtdcProductGroupField pProductGroup, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryMMInstrumentCommissionRate(CThostFtdcMMInstrumentCommissionRateField pMMInstrumentCommissionRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryMMOptionInstrCommRate(CThostFtdcMMOptionInstrCommRateField pMMOptionInstrCommRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInstrumentOrderCommRate(CThostFtdcInstrumentOrderCommRateField pInstrumentOrderCommRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQrySecAgentTradingAccount(CThostFtdcTradingAccountField pTradingAccount, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQrySecAgentCheckMode(CThostFtdcSecAgentCheckModeField pSecAgentCheckMode, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryOptionInstrTradeCost(CThostFtdcOptionInstrTradeCostField pOptionInstrTradeCost, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryOptionInstrCommRate(CThostFtdcOptionInstrCommRateField pOptionInstrCommRate, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryExecOrder(CThostFtdcExecOrderField pExecOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryForQuote(CThostFtdcForQuoteField pForQuote, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryQuote(CThostFtdcQuoteField pQuote, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryOptionSelfClose(CThostFtdcOptionSelfCloseField pOptionSelfClose, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryInvestUnit(CThostFtdcInvestUnitField pInvestUnit, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryCombInstrumentGuard(CThostFtdcCombInstrumentGuardField pCombInstrumentGuard, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryCombAction(CThostFtdcCombActionField pCombAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryTransferSerial(CThostFtdcTransferSerialField pTransferSerial, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryAccountregister(CThostFtdcAccountregisterField pAccountregister, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	// 定单回报
	public void OnRtnOrder(CThostFtdcOrderField pOrder) {
		try {
			String symbol = pOrder.getInstrumentID();

			// 无法获取账户信息,使用userId作为账户ID
			String accountCode = settings.getUserId();
			// 无法获取币种信息使用特定值CNY
			String accountId = accountCode + "@" + gatewayId;

			int frontId = pOrder.getFrontID();
			int sessionId = pOrder.getSessionID();
			String orderRef = StringUtils.trim(pOrder.getOrderRef());

			String adapterOrderId = frontId + "_" + sessionId + "_" + orderRef;
			String orderId = gatewayId + "@" + adapterOrderId;

			String exchangeAndOrderSysId = pOrder.getExchangeID() + "@" + pOrder.getOrderSysID();

			exchangeIdAndOrderSysIdToOrderIdMap.put(exchangeAndOrderSysId, orderId);
			orderIdToOrderRefMap.put(orderId, orderRef);
			orderIdToAdapterOrderIdMap.put(orderId, adapterOrderId);

			DirectionEnum direction = CtpConstant.directionMapReverse.getOrDefault(pOrder.getDirection(), DirectionEnum.D_Unknown);
			OffsetFlagEnum offsetFlag = CtpConstant.offsetMapReverse.getOrDefault(pOrder.getCombOffsetFlag().toCharArray()[0], OffsetFlagEnum.OF_Unknown);

			double price = pOrder.getLimitPrice();

			int totalVolume = pOrder.getVolumeTotalOriginal();
			int tradedVolume = pOrder.getVolumeTraded();

			OrderStatusEnum orderStatus = CtpConstant.statusMapReverse.get(pOrder.getOrderStatus());
			String statusMsg = pOrder.getStatusMsg();

			String orderDate = pOrder.getInsertDate();
			String orderTime = pOrder.getInsertTime();
			LocalDateTime tradeDatetime = LocalDateTime.of(LocalDate.from(DateTimeConstant.D_FORMAT_INT_FORMATTER.parse(orderDate)), LocalTime.from(DateTimeConstant.T_FORMAT_FORMATTER.parse(orderTime)));
			long tradeTimestamp = tradeDatetime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1000;
			String activeTime = String.valueOf(tradeTimestamp);
			String cancelTime = pOrder.getCancelTime();
			String updateTime = pOrder.getUpdateTime();
			String suspendTime = pOrder.getSuspendTime();

			HedgeFlagEnum hedgeFlag = CtpConstant.hedgeFlagMapReverse.getOrDefault(pOrder.getCombHedgeFlag(), HedgeFlagEnum.HF_Unknown);
			ContingentConditionEnum contingentCondition = CtpConstant.contingentConditionMapReverse.getOrDefault(pOrder.getContingentCondition(), ContingentConditionEnum.CC_Unknown);
			ForceCloseReasonEnum forceCloseReason = CtpConstant.forceCloseReasonMapReverse.getOrDefault(pOrder.getForceCloseReason(), ForceCloseReasonEnum.FCR_Unknown);
			TimeConditionEnum timeCondition = CtpConstant.timeConditionMapReverse.getOrDefault(pOrder.getTimeCondition(), TimeConditionEnum.TC_Unknown);

			int userForceClose = pOrder.getUserForceClose();
			String gtdDate = pOrder.getGTDDate();
			int autoSuspend = pOrder.getIsAutoSuspend();
			int swapOrder = pOrder.getIsSwapOrder();

			VolumeConditionEnum volumeCondition = CtpConstant.volumeConditionMapReverse.getOrDefault(pOrder.getVolumeCondition(), VolumeConditionEnum.VC_Unknown);
			OrderPriceTypeEnum orderPriceType = CtpConstant.orderPriceTypeMapReverse.getOrDefault(pOrder.getOrderPriceType(), OrderPriceTypeEnum.OPT_Unknown);

			int minVolume = pOrder.getMinVolume();
			double stopPrice = pOrder.getStopPrice();

			String orderLocalId = StringUtils.trim(pOrder.getOrderLocalID());
			String orderSysId = StringUtils.trim(pOrder.getOrderSysID());
			String sequenceNo = pOrder.getSequenceNo() + "";
			String brokerOrderSeq = pOrder.getBrokerOrderSeq() + "";

			String originalOrderId = orderIdToOriginalOrderIdMap.getOrDefault(orderId, "");

			OrderField.Builder orderBuilder = OrderField.newBuilder();
			orderBuilder.setAccountId(accountId);
			orderBuilder.setActiveTime(activeTime);
			orderBuilder.setAdapterOrderId(adapterOrderId);
			orderBuilder.setCancelTime(cancelTime);
			orderBuilder.setDirection(direction);
			orderBuilder.setFrontId(frontId);
			orderBuilder.setOffsetFlag(offsetFlag);
			orderBuilder.setOrderDate(orderDate);
			orderBuilder.setOrderId(orderId);
			orderBuilder.setOrderStatus(orderStatus);
			orderBuilder.setOrderTime(orderTime);
			orderBuilder.setOriginOrderId(originalOrderId);
			orderBuilder.setPrice(price);
			orderBuilder.setSessionId(sessionId);
			orderBuilder.setTotalVolume(totalVolume);
			orderBuilder.setTradedVolume(tradedVolume);
			orderBuilder.setTradingDay(tradingDay);
			orderBuilder.setUpdateTime(updateTime);
			orderBuilder.setStatusMsg(statusMsg);
			orderBuilder.setGatewayId(gatewayId);
			orderBuilder.setHedgeFlag(hedgeFlag);
			orderBuilder.setContingentCondition(contingentCondition);
			orderBuilder.setForceCloseReason(forceCloseReason);
			orderBuilder.setTimeCondition(timeCondition);
			orderBuilder.setGtdDate(gtdDate);
			orderBuilder.setAutoSuspend(autoSuspend);
			orderBuilder.setVolumeCondition(volumeCondition);
			orderBuilder.setMinVolume(minVolume);
			orderBuilder.setStopPrice(stopPrice);
			orderBuilder.setUserForceClose(userForceClose);
			orderBuilder.setSwapOrder(swapOrder);
			orderBuilder.setSuspendTime(suspendTime);
			orderBuilder.setOrderLocalId(orderLocalId);
			orderBuilder.setOrderSysId(orderSysId);
			orderBuilder.setSequenceNo(sequenceNo);
			orderBuilder.setBrokerOrderSeq(brokerOrderSeq);
			orderBuilder.setOrderPriceType(orderPriceType);

			if (instrumentQueried) {
				orderBuilder.setContract(gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol).contractField());
				OrderField order = orderBuilder.build();
				orderIdToOrderMap.put(order.getOrderId(), order);
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.ORDER, order);
			} else {
				ContractField.Builder contractBuilder = ContractField.newBuilder();
				contractBuilder.setSymbol(symbol);
				orderBuilder.setContract(contractBuilder);
				orderBuilderCacheList.add(orderBuilder);
			}
			logger.info("{}委托回报：合约{}，单号{}，方向{}，开平{}，价格{}，止损{}，手数{}，交易日{}，类型{} & {}，状态{}", logInfo, 
					symbol, originalOrderId, direction, offsetFlag, price, stopPrice, tradedVolume, tradingDay, hedgeFlag, timeCondition, orderStatus);
		} catch (Throwable t) {
			logger.error("{}OnRtnOrder Exception", logInfo, t);
		}
	}

	// 成交回报
	public void OnRtnTrade(CThostFtdcTradeField pTrade) {
		try {

			String exchangeAndOrderSysId = pTrade.getExchangeID() + "@" + pTrade.getOrderSysID();

			String orderId = exchangeIdAndOrderSysIdToOrderIdMap.getOrDefault(exchangeAndOrderSysId, "");
			String adapterOrderId = orderIdToAdapterOrderIdMap.getOrDefault(orderId, "");

			String symbol = pTrade.getInstrumentID();
			DirectionEnum direction = CtpConstant.directionMapReverse.getOrDefault(pTrade.getDirection(), DirectionEnum.D_Unknown);
			String adapterTradeId = adapterOrderId + "@" + direction.getValueDescriptor().getName() + "@" + StringUtils.trim(pTrade.getTradeID());
			String tradeId = gatewayId + "@" + adapterTradeId;
			OffsetFlagEnum offsetFlag = CtpConstant.offsetMapReverse.getOrDefault(pTrade.getOffsetFlag(), OffsetFlagEnum.OF_Unknown);
			double price = pTrade.getPrice();
			int volume = pTrade.getVolume();
			String tradeDate = pTrade.getTradeDate();
			String tradeTime = pTrade.getTradeTime();
			LocalDateTime tradeDatetime = LocalDateTime.of(LocalDate.from(DateTimeConstant.D_FORMAT_INT_FORMATTER.parse(tradeDate)), LocalTime.from(DateTimeConstant.T_FORMAT_FORMATTER.parse(tradeTime)));
			long tradeTimestamp = tradeDatetime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1000;

			HedgeFlagEnum hedgeFlag = CtpConstant.hedgeFlagMapReverse.getOrDefault(String.valueOf(pTrade.getHedgeFlag()), HedgeFlagEnum.HF_Unknown);
			TradeTypeEnum tradeType = CtpConstant.tradeTypeMapReverse.getOrDefault(pTrade.getTradeType(), TradeTypeEnum.TT_Unknown);
			PriceSourceEnum priceSource = CtpConstant.priceSourceMapReverse.getOrDefault(pTrade.getPriceSource(), PriceSourceEnum.PSRC_Unknown);

			String orderLocalId = StringUtils.trim(pTrade.getOrderLocalID());
			String orderSysId = StringUtils.trim(pTrade.getOrderSysID());
			String sequenceNo = pTrade.getSequenceNo() + "";
			String brokerOrderSeq = pTrade.getBrokerOrderSeq() + "";
			String settlementID = pTrade.getSettlementID() + "";

			String originalOrderId = orderIdToOriginalOrderIdMap.getOrDefault(orderId, "");

			// 无法获取账户信息,使用userId作为账户ID
			String accountCode = settings.getUserId();
			// 无法获取币种信息使用特定值CNY
			String accountId = accountCode + "@" + gatewayId;

			TradeField.Builder tradeBuilder = TradeField.newBuilder();

			tradeBuilder.setAccountId(accountId);
			tradeBuilder.setAdapterOrderId(adapterOrderId);
			tradeBuilder.setAdapterTradeId(adapterTradeId);
			tradeBuilder.setTradeDate(tradeDate);
			tradeBuilder.setTradeId(tradeId);
			tradeBuilder.setTradeTime(tradeTime);
			tradeBuilder.setTradingDay(tradingDay);
			tradeBuilder.setTradeTimestamp(tradeTimestamp);
			tradeBuilder.setDirection(direction);
			tradeBuilder.setOffsetFlag(offsetFlag);
			tradeBuilder.setOrderId(orderId);
			tradeBuilder.setOriginOrderId(originalOrderId);
			tradeBuilder.setPrice(price);
			tradeBuilder.setVolume(volume);
			tradeBuilder.setGatewayId(gatewayId);
			tradeBuilder.setOrderLocalId(orderLocalId);
			tradeBuilder.setOrderSysId(orderSysId);
			tradeBuilder.setSequenceNo(sequenceNo);
			tradeBuilder.setBrokerOrderSeq(brokerOrderSeq);
			tradeBuilder.setSettlementId(settlementID);
			tradeBuilder.setHedgeFlag(hedgeFlag);
			tradeBuilder.setTradeType(tradeType);
			tradeBuilder.setPriceSource(priceSource);

			if (instrumentQueried) {
				ContractField contract = gatewayAdapter.mktCenter.getContract(MKT_GATEWAY_ID, symbol).contractField();
				price = (int)(price / contract.getPriceTick()) * contract.getPriceTick();
				tradeBuilder.setContract(contract);
				tradeBuilder.setPrice(price);	// 优化价格精度
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.TRADE, tradeBuilder.build());
			} else {
				ContractField.Builder contractBuilder = ContractField.newBuilder();
				contractBuilder.setSymbol(symbol);

				tradeBuilder.setContract(contractBuilder);
				tradeBuilderCacheList.add(tradeBuilder);
			}
			logger.info("{}成交回报：合约{}，单号{}，方向{}，开平{}，价格{}，手数{}，交易日{}，类型{} & {}", logInfo, 
					symbol, originalOrderId, direction, offsetFlag, price, volume, tradingDay, hedgeFlag, tradeType);
		} catch (Throwable t) {
			logger.error("{}OnRtnTrade Exception", logInfo, t);
		}

	}

	// 发单错误回报
	public void OnErrRtnOrderInsert(CThostFtdcInputOrderField pInputOrder, CThostFtdcRspInfoField pRspInfo) {
		try {
			logger.error("{}交易接口发单错误回报（OnErrRtnOrderInsert） 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
			logger.error(
					"{}交易接口发单错误回报（OnErrRtnOrderInsert） 定单详细信息 ->{InstrumentID:{}, LimitPrice:{}, VolumeTotalOriginal:{}, OrderPriceType:{}, Direction:{}, CombOffsetFlag:{}, OrderRef:{}, InvestorID:{}, UserID:{}, BrokerID:{}, ExchangeID:{}, CombHedgeFlag:{}, ContingentCondition:{}, ForceCloseReason:{}, IsAutoSuspend:{}, IsSwapOrder:{}, MinVolume:{}, TimeCondition:{}, VolumeCondition:{}, StopPrice:{}}", //
					logInfo, //
					pInputOrder.getInstrumentID(), //
					pInputOrder.getLimitPrice(), //
					pInputOrder.getVolumeTotalOriginal(), //
					pInputOrder.getOrderPriceType(), //
					pInputOrder.getDirection(), //
					pInputOrder.getCombOffsetFlag(), //
					pInputOrder.getOrderRef(), //
					pInputOrder.getInvestorID(), //
					pInputOrder.getUserID(), //
					pInputOrder.getBrokerID(), //
					pInputOrder.getExchangeID(), //
					pInputOrder.getCombHedgeFlag(), //
					pInputOrder.getContingentCondition(), //
					pInputOrder.getForceCloseReason(), //
					pInputOrder.getIsAutoSuspend(), //
					pInputOrder.getIsSwapOrder(), //
					pInputOrder.getMinVolume(), //
					pInputOrder.getTimeCondition(), //
					pInputOrder.getVolumeCondition(), //
					pInputOrder.getStopPrice());

			if (instrumentQueried) {
				NoticeField notice = NoticeField.newBuilder()
					.setContent(logInfo + "交易接口发单错误回报，错误ID:" + pRspInfo.getErrorID() + "，错误信息:" + pRspInfo.getErrorMsg())
					.setStatus(CommonStatusEnum.COMS_ERROR)
					.setTimestamp(System.currentTimeMillis()).build();
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
			}
		} catch (Throwable t) {
			logger.error("{}OnErrRtnOrderInsert Exception", logInfo, t);
		}

	}

	// 撤单错误回报
	public void OnErrRtnOrderAction(CThostFtdcOrderActionField pOrderAction, CThostFtdcRspInfoField pRspInfo) {
		if (pRspInfo != null) {
			logger.error("{}交易接口撤单错误(OnErrRtnOrderAction) 错误ID:{},错误信息:{}", logInfo, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
			if (instrumentQueried) {
				NoticeField notice = NoticeField.newBuilder()
					.setContent(logInfo + "交易接口撤单错误回报，错误ID:" + pRspInfo.getErrorID() + "，错误信息:" + pRspInfo.getErrorMsg())
					.setStatus(CommonStatusEnum.COMS_ERROR)
					.setTimestamp(System.currentTimeMillis())
					.build();
				gatewayAdapter.getEventEngine().emitEvent(NorthstarEventType.NOTICE, notice);
			}
		} else {
			logger.error("{}处理交易接口撤单错误(OnErrRtnOrderAction)错误,无有效信息", logInfo);
		}
	}

	public void OnRtnInstrumentStatus(CThostFtdcInstrumentStatusField pInstrumentStatus) {
	}

	public void OnRtnBulletin(CThostFtdcBulletinField pBulletin) {
	}

	public void OnRtnTradingNotice(CThostFtdcTradingNoticeInfoField pTradingNoticeInfo) {
	}

	public void OnRtnErrorConditionalOrder(CThostFtdcErrorConditionalOrderField pErrorConditionalOrder) {
	}

	public void OnRtnExecOrder(CThostFtdcExecOrderField pExecOrder) {
	}

	public void OnErrRtnExecOrderInsert(CThostFtdcInputExecOrderField pInputExecOrder, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnExecOrderAction(CThostFtdcExecOrderActionField pExecOrderAction, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnForQuoteInsert(CThostFtdcInputForQuoteField pInputForQuote, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRtnQuote(CThostFtdcQuoteField pQuote) {
	}

	public void OnErrRtnQuoteInsert(CThostFtdcInputQuoteField pInputQuote, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnQuoteAction(CThostFtdcQuoteActionField pQuoteAction, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRtnForQuoteRsp(CThostFtdcForQuoteRspField pForQuoteRsp) {
	}

	public void OnRtnCFMMCTradingAccountToken(CThostFtdcCFMMCTradingAccountTokenField pCFMMCTradingAccountToken) {
	}

	public void OnErrRtnBatchOrderAction(CThostFtdcBatchOrderActionField pBatchOrderAction, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRtnOptionSelfClose(CThostFtdcOptionSelfCloseField pOptionSelfClose) {
	}

	public void OnErrRtnOptionSelfCloseInsert(CThostFtdcInputOptionSelfCloseField pInputOptionSelfClose, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnOptionSelfCloseAction(CThostFtdcOptionSelfCloseActionField pOptionSelfCloseAction, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRtnCombAction(CThostFtdcCombActionField pCombAction) {
	}

	public void OnErrRtnCombActionInsert(CThostFtdcInputCombActionField pInputCombAction, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRspQryContractBank(CThostFtdcContractBankField pContractBank, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryParkedOrder(CThostFtdcParkedOrderField pParkedOrder, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryParkedOrderAction(CThostFtdcParkedOrderActionField pParkedOrderAction, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryTradingNotice(CThostFtdcTradingNoticeField pTradingNotice, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryBrokerTradingParams(CThostFtdcBrokerTradingParamsField pBrokerTradingParams, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQryBrokerTradingAlgos(CThostFtdcBrokerTradingAlgosField pBrokerTradingAlgos, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQueryCFMMCTradingAccountToken(CThostFtdcQueryCFMMCTradingAccountTokenField pQueryCFMMCTradingAccountToken, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRtnFromBankToFutureByBank(CThostFtdcRspTransferField pRspTransfer) {
	}

	public void OnRtnFromFutureToBankByBank(CThostFtdcRspTransferField pRspTransfer) {
	}

	public void OnRtnRepealFromBankToFutureByBank(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRtnRepealFromFutureToBankByBank(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRtnFromBankToFutureByFuture(CThostFtdcRspTransferField pRspTransfer) {
	}

	public void OnRtnFromFutureToBankByFuture(CThostFtdcRspTransferField pRspTransfer) {
	}

	public void OnRtnRepealFromBankToFutureByFutureManual(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRtnRepealFromFutureToBankByFutureManual(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRtnQueryBankBalanceByFuture(CThostFtdcNotifyQueryAccountField pNotifyQueryAccount) {
	}

	public void OnErrRtnBankToFutureByFuture(CThostFtdcReqTransferField pReqTransfer, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnFutureToBankByFuture(CThostFtdcReqTransferField pReqTransfer, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnRepealBankToFutureByFutureManual(CThostFtdcReqRepealField pReqRepeal, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnRepealFutureToBankByFutureManual(CThostFtdcReqRepealField pReqRepeal, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnErrRtnQueryBankBalanceByFuture(CThostFtdcReqQueryAccountField pReqQueryAccount, CThostFtdcRspInfoField pRspInfo) {
	}

	public void OnRtnRepealFromBankToFutureByFuture(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRtnRepealFromFutureToBankByFuture(CThostFtdcRspRepealField pRspRepeal) {
	}

	public void OnRspFromBankToFutureByFuture(CThostFtdcReqTransferField pReqTransfer, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspFromFutureToBankByFuture(CThostFtdcReqTransferField pReqTransfer, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRspQueryBankAccountMoneyByFuture(CThostFtdcReqQueryAccountField pReqQueryAccount, CThostFtdcRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
	}

	public void OnRtnOpenAccountByBank(CThostFtdcOpenAccountField pOpenAccount) {
	}

	public void OnRtnCancelAccountByBank(CThostFtdcCancelAccountField pCancelAccount) {
	}

	public void OnRtnChangeAccountByBank(CThostFtdcChangeAccountField pChangeAccount) {
	}
}