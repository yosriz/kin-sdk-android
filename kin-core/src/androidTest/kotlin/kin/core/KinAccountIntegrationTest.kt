package kin.core

import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import kin.core.IntegConsts.TEST_NETWORK_URL
import kin.core.exception.AccountNotActivatedException
import kin.core.exception.AccountNotFoundException
import kin.core.exception.InsufficientKinException
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.rules.ExpectedException
import org.stellar.sdk.Memo
import org.stellar.sdk.MemoText
import org.stellar.sdk.Server
import java.io.IOException
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.fail

@Suppress("FunctionName")
class KinAccountIntegrationTest {

    private val appId = "1a2c"
    private val appIdVersionPrefix = "1"

    private lateinit var kinClient: KinClient

    @Rule
    @JvmField
    val expectedEx: ExpectedException = ExpectedException.none()

    private val environment: Environment = Environment(IntegConsts.TEST_NETWORK_URL, IntegConsts.TEST_NETWORK_ID, fakeKinIssuer.accountId)

    @Before
    @Throws(IOException::class)
    fun setup() {
        kinClient = KinClient(InstrumentationRegistry.getTargetContext(), environment, appId)
        kinClient.clearAllAccounts()
    }

    @After
    fun teardown() {
        if (::kinClient.isInitialized) {
            kinClient.clearAllAccounts()
        }
    }

    @Test
    @LargeTest
    fun getBalanceSync_AccountNotCreated_AccountNotFoundException() {
        val kinAccount = kinClient.addAccount()

        expectedEx.expect(AccountNotFoundException::class.java)
        expectedEx.expectMessage(kinAccount.publicAddress.orEmpty())
        kinAccount.balanceSync
    }

    @Test
    @LargeTest
    fun getStatusSync_AccountNotCreated_StatusNotCreated() {
        val kinAccount = kinClient.addAccount()

        val status = kinAccount.statusSync
        assertThat(status, equalTo(AccountStatus.NOT_CREATED))
    }

    @Test
    @LargeTest
    fun getBalanceSync_AccountNotActivated_AccountNotActivatedException() {
        val kinAccount = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccount.publicAddress.orEmpty())

        expectedEx.expect(AccountNotActivatedException::class.java)
        expectedEx.expectMessage(kinAccount.publicAddress.orEmpty())
        kinAccount.balanceSync
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun getStatusSync_AccountNotActivated_StatusNotActivated() {
        val kinAccount = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccount.publicAddress.orEmpty())

        val status = kinAccount.statusSync
        assertThat(status, equalTo(AccountStatus.NOT_ACTIVATED))
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun getBalanceSync_FundedAccount_GotBalance() {
        val kinAccount = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccount.publicAddress.orEmpty())

        kinAccount.activateSync()
        assertThat(kinAccount.balanceSync.value(), equalTo(BigDecimal("0.0000000")))

        fakeKinIssuer.fundWithKin(kinAccount.publicAddress.orEmpty(), "3.1415926")
        assertThat(kinAccount.balanceSync.value(), equalTo(BigDecimal("3.1415926")))
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun getStatusSync_CreateAndActivateAccount_StatusActivated() {
        val kinAccount = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccount.publicAddress.orEmpty())

        kinAccount.activateSync()
        assertThat(kinAccount.balanceSync.value(), equalTo(BigDecimal("0.0000000")))
        val status = kinAccount.statusSync
        assertThat(status, equalTo(AccountStatus.ACTIVATED))
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun activateSync_AccountNotCreated_AccountNotFoundException() {
        val kinAccount = kinClient.addAccount()

        expectedEx.expect(AccountNotFoundException::class.java)
        expectedEx.expectMessage(kinAccount.publicAddress.orEmpty())
        kinAccount.activateSync()
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_WithMemo() {
        val (kinAccountSender, kinAccountReceiver) = onboardAccounts(senderFundAmount = 100)

        val memo = "fake memo"
        val expectedMemo = addAppIdToMemo(memo)

        val latch = CountDownLatch(1)
        val listenerRegistration = kinAccountReceiver.addPaymentListener { _ -> latch.countDown() }

        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(),
                BigDecimal("21.123"), memo)
        val transactionId = kinAccountSender.sendTransactionSync(transaction)
        assertThat(kinAccountSender.balanceSync.value(), equalTo(BigDecimal("78.8770000")))
        assertThat(kinAccountReceiver.balanceSync.value(), equalTo(BigDecimal("21.1230000")))

        latch.await(10, TimeUnit.SECONDS)
        listenerRegistration.remove()

        val server = Server(TEST_NETWORK_URL)
        val transactionResponse = server.transactions().transaction(transactionId.id())
        val actualMemo = transactionResponse.memo
        assertThat<Memo>(actualMemo, `is`<Memo>(instanceOf<Memo>(MemoText::class.java)))
        assertThat((actualMemo as MemoText).text, equalTo(expectedMemo))
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_ReceiverAccountNotCreated_AccountNotFoundException() {
        val kinAccountSender = kinClient.addAccount()
        val kinAccountReceiver = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccountSender.publicAddress.orEmpty())
        kinAccountSender.activateSync()

        expectedEx.expect(AccountNotFoundException::class.java)
        expectedEx.expectMessage(kinAccountReceiver.publicAddress)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"))
        kinAccountSender.sendTransactionSync(transaction)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_SenderAccountNotCreated_AccountNotFoundException() {
        val kinAccountSender = kinClient.addAccount()
        val kinAccountReceiver = kinClient.addAccount()
        fakeKinIssuer.createAccount(kinAccountReceiver.publicAddress.orEmpty())

        expectedEx.expect(AccountNotFoundException::class.java)
        expectedEx.expectMessage(kinAccountSender.publicAddress)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"))
        kinAccountSender.sendTransactionSync(transaction)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_ReceiverAccountNotActivated_AccountNotFoundException() {
        val (kinAccountSender, kinAccountReceiver) = onboardAccounts(activateSender = true, activateReceiver = false)

        expectedEx.expect(AccountNotActivatedException::class.java)
        expectedEx.expectMessage(kinAccountReceiver.publicAddress)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"))
        kinAccountSender.sendTransactionSync(transaction)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_SenderAccountNotActivated_AccountNotFoundException() {
        val (kinAccountSender, kinAccountReceiver) = onboardAccounts(activateSender = false, activateReceiver = true)

        fakeKinIssuer.createAccount(kinAccountSender.publicAddress.orEmpty())
        fakeKinIssuer.createAccount(kinAccountReceiver.publicAddress.orEmpty())

        kinAccountReceiver.activateSync()

        expectedEx.expect(AccountNotActivatedException::class.java)
        expectedEx.expectMessage(kinAccountSender.publicAddress)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"))
        kinAccountSender.sendTransactionSync(transaction)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun createPaymentListener_ListenToReceiver_PaymentEvent() {
        listenToPayments(false)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun createPaymentListener_ListenToSender_PaymentEvent() {
        listenToPayments(true)
    }

    @Throws(Exception::class)
    private fun listenToPayments(sender: Boolean) {
        //create and sets 2 accounts (receiver/sender), fund one account, and then
        //send transaction from the funded account to the other, observe this transaction using listeners
        val fundingAmount = BigDecimal("100")
        val transactionAmount = BigDecimal("21.123")

        val (kinAccountSender, kinAccountReceiver) = onboardAccounts(true, true, 0, 0)

        //register listeners for testing
        val actualPaymentsResults = ArrayList<PaymentInfo>()
        val actualBalanceResults = ArrayList<Balance>()
        val accountToListen = if (sender) kinAccountSender else kinAccountReceiver

        val eventsCount = if (sender) 4 else 2 ///in case of observing the sender we'll get 2 events (1 for funding 1 for the
        //transaction) in case of receiver - only 1 event. multiply by 2, as we 2 listeners (balance and payment)
        val latch = CountDownLatch(eventsCount)
        val paymentListener = accountToListen.addPaymentListener { data ->
            actualPaymentsResults.add(data)
            latch.countDown()
        }
        val balanceListener = accountToListen.addBalanceListener { data ->
            actualBalanceResults.add(data)
            latch.countDown()
        }

        //send the transaction we want to observe
        fakeKinIssuer.fundWithKin(kinAccountSender.publicAddress.orEmpty(), "100")
        val memo = "memo"
        val expectedMemo = addAppIdToMemo(memo)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), transactionAmount, memo)
        val expectedTransactionId = kinAccountSender.sendTransactionSync(transaction)

        //verify data notified by listeners
        val transactionIndex = if (sender) 1 else 0 //in case of observing the sender we'll get 2 events (1 for funding 1 for the
        //transaction) in case of receiver - only 1 event
        latch.await(10, TimeUnit.SECONDS)
        paymentListener.remove()
        balanceListener.remove()
        val paymentInfo = actualPaymentsResults[transactionIndex]
        assertThat(paymentInfo.amount(), equalTo(transactionAmount))
        assertThat(paymentInfo.destinationPublicKey(), equalTo(kinAccountReceiver.publicAddress))
        assertThat(paymentInfo.sourcePublicKey(), equalTo(kinAccountSender.publicAddress))
        assertThat(paymentInfo.memo(), equalTo(expectedMemo))
        assertThat(paymentInfo.hash().id(), equalTo(expectedTransactionId.id()))

        val balance = actualBalanceResults[transactionIndex]
        assertThat(balance.value(),
                equalTo(if (sender) fundingAmount.subtract(transactionAmount) else transactionAmount))
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun createPaymentListener_RemoveListener_NoEvents() {
        val (kinAccountSender, kinAccountReceiver) = onboardAccounts(senderFundAmount = 100)

        val latch = CountDownLatch(1)
        val listenerRegistration = kinAccountReceiver.addPaymentListener {
            fail("should not get eny event!")
        }
        listenerRegistration.remove()

        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"), null)
        kinAccountSender.sendTransactionSync(transaction)

        latch.await(15, TimeUnit.SECONDS)
    }

    @Test
    @LargeTest
    @Throws(Exception::class)
    fun sendTransaction_NotEnoughKin_TransactionFailedException() {
        val (kinAccountSender, kinAccountReceiver) = onboardAccounts()

        expectedEx.expect(InsufficientKinException::class.java)
        val transaction = kinAccountSender.buildTransactionSync(kinAccountReceiver.publicAddress.orEmpty(), BigDecimal("21.123"))
        kinAccountSender.sendTransactionSync(transaction)
    }

    private fun onboardAccounts(activateSender: Boolean = true, activateReceiver: Boolean = true, senderFundAmount: Int = 0,
                                receiverFundAmount: Int = 0): Pair<KinAccount, KinAccount> {
        val kinAccountSender = kinClient.addAccount()
        val kinAccountReceiver = kinClient.addAccount()
        onboardSingleAccount(kinAccountSender, activateSender, senderFundAmount)
        onboardSingleAccount(kinAccountReceiver, activateReceiver, receiverFundAmount)
        return Pair(kinAccountSender, kinAccountReceiver)
    }

    private fun onboardSingleAccount(account: KinAccount, shouldActivate: Boolean, fundAmount: Int) {
        fakeKinIssuer.createAccount(account.publicAddress.orEmpty())
        if (shouldActivate) {
            account.activateSync()
        }
        if (fundAmount > 0) {
            fakeKinIssuer.fundWithKin(account.publicAddress.orEmpty(), fundAmount.toString())
        }
    }

    private fun addAppIdToMemo(memo: String): String {
        return appIdVersionPrefix.plus("-").plus(appId).plus("-").plus(memo);
    }

    companion object {

        private lateinit var fakeKinIssuer: FakeKinIssuer

        @BeforeClass
        @JvmStatic
        @Throws(IOException::class)
        fun setupKinIssuer() {
            fakeKinIssuer = FakeKinIssuer()
        }
    }
}