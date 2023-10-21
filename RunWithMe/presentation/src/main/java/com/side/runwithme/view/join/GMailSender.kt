package com.side.runwithme.view.join

import java.util.Properties
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.util.ByteArrayDataSource


class GMailSender(val user: String, val password: String): javax.mail.Authenticator() {
    private val mailHost = "smtp.gmail.com"
    private lateinit var session: Session
    private var emailCode: String = ""
    private val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    init {
        initProps()
    }
    private fun initProps(){
        val props = Properties()
        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", mailHost)
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.socketFactory.fallback"] = "false"
        props.setProperty("mail.smtp.quitwait", "false")

        //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
        session = Session.getDefaultInstance(props, this)
    }



    //생성된 이메일 인증코드 반환
    fun getEmailCode(): String {
        return emailCode
    }

    //이메일 인증코드 생성
    fun createEmailCode() {
        emailCode = (1..6)
            .map { charset.random() }
            .joinToString("")
    }

    override fun getPasswordAuthentication(): PasswordAuthentication {
        //해당 메서드에서 사용자의 계정(id & password)을 받아 인증받으며 인증 실패시 기본값으로 반환됨.
        return PasswordAuthentication(user, password)
    }

    @Synchronized
    @Throws(Exception::class)
    fun sendMail(subject: String?, body: String, recipients: String) {

        val message = MimeMessage(session)
        val handler = DataHandler(
            ByteArrayDataSource(
                body.toByteArray(),
                "text/html"
            )
        ) //본문 내용을 byte단위로 쪼개어 전달
        message.sender = InternetAddress(user) //본인 이메일 설정
        message.subject = subject //해당 이메일의 본문 설정
        message.dataHandler = handler
        if (recipients.indexOf(',') > 0) message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(recipients)
        ) else message.setRecipient(Message.RecipientType.TO, InternetAddress(recipients))
        Transport.send(message) //메시지 전달
    }
}