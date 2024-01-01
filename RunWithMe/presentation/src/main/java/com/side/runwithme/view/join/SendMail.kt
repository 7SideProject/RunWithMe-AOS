package com.side.runwithme.view.join

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.side.runwithme.BuildConfig
import com.side.runwithme.R
import javax.mail.MessagingException
import javax.mail.SendFailedException


class SendMail: AppCompatActivity() {
    private val user = BuildConfig.MAIL_ID
    private val password = BuildConfig.MAIL_PASSWORD
    private val gMailSender = GMailSender(user,password)

    fun sendSecurityCode(context: Context, sendTo: String){
        try {
            gMailSender.createEmailCode()
            gMailSender.sendMail(context.resources.getString(R.string.verify_mail_title), returnMailBody(), sendTo)
        } catch (e: SendFailedException) {
            Log.d("test5", "1sendSecurityCode: $e")
        } catch (e: MessagingException) {
        } catch (e: Exception) {
            Log.d("test5", "3sendSecurityCode: $e")
            e.printStackTrace()
        }
    }

    fun getEmailCode(): String{
        return gMailSender.getEmailCode()
    }

    private fun returnMailBody(): String{
        return "<!DOCTYPE html><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><link rel=\"stylesheet\" type=\"text/css\" id=\"u0\" href=\"https://ko.rakko.tools/tools/129/lib/tinymce/skins/ui/oxide/content.min.css\"><link rel=\"stylesheet\" type=\"text/css\" id=\"u1\" href=\"https://ko.rakko.tools/tools/129/lib/tinymce/skins/content/default/content.min.css\"></head><body id=\"tinymce\" class=\"mce-content-body\" data-id=\"content\" contenteditable=\"true\" spellcheck=\"false\"><p><img src=\"https://cdn.pixabay.com/photo/2021/05/14/08/44/running-6252827_1280.jpg\" width=\"400\" height=\"267\" alt=\"\" style=\"display: block; margin-left: auto; margin-right: auto;\" data-mce-style=\"display: block; margin-left: auto; margin-right: auto;\"></p><h3 data-mce-style=\"text-align: center;\" style=\"text-align: center;\">안녕하세요</h3><h3 data-mce-style=\"text-align: center;\" style=\"text-align: center;\">RunWithMe입니다.</h3><p style=\"text-align: center;\" data-mce-style=\"text-align: center;\"><br data-mce-bogus=\"1\"></p><h3 data-mce-style=\"text-align: center;\" style=\"text-align: center;\">이메일 인증을 위하여 <span style=\"color: rgb(0, 0, 0);\" data-mce-style=\"color: #000000;\"><strong>[${getEmailCode()}]</strong></span> 를 입력해주세요.&nbsp;</h3><p><br data-mce-bogus=\"1\"></p><p><br data-mce-bogus=\"1\"></p><p style=\"text-align: center;\" data-mce-style=\"text-align: center;\"><br data-mce-bogus=\"1\"></p></body></html>"
    }
}