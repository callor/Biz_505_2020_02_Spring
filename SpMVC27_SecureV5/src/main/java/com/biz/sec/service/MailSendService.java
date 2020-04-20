package com.biz.sec.service;

import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.biz.sec.domain.UserDetailsVO;
import com.biz.sec.utils.PbeEncryptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailSendService {
	
	private final JavaMailSender javaMailSender;
	private final String from_email = "callor88@naver.com";

	public MailSendService(
		@Qualifier("gmailMailHander") 
		JavaMailSender javaMailSender) {
		
		super();
		this.javaMailSender = javaMailSender;
	
	}

	public void sendMail() {
		
		
		String to_email = "callor@callor.com";
		String subject = "메일보내기 테스트";
		String content = "반갑습니다";
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper mHelper ;
		mHelper = new MimeMessageHelper(message,"UTF-8");
		
		try {

			mHelper.setFrom(from_email);
			mHelper.setTo(to_email);
			mHelper.setSubject(subject);
			
			// true : 메일본문에 html 효과주기
			mHelper.setText(content,true); 
			javaMailSender.send(message);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	/**
	 * 회원가입된 사용자에게 인증 email을 전송
	 * 
	 * username을 암호화 시키고
	 * email 인증을 수행할수 있는 링크를 email 본문에 작성하여
	 * 전송을 한다.
	 * 
	 * @param userVO
	 * @return
	 */
	// public boolean join_send(UserDetailsVO userVO) {
	public String join_send(UserDetailsVO userVO) {
			
		String userName = userVO.getUsername();
		String email = userVO.getEmail();
		
		String encUserName = PbeEncryptor.getEncrypt(userName);
		String encEmail = PbeEncryptor.getEncrypt(email);
		
		// localhost:8080/sec/join/email/callor/callor@callor.com
		StringBuilder email_link = new StringBuilder(); 
		email_link.append("http://localhost:8080/sec/");
		email_link.append("join/emailok/");
		email_link.append(
				Base64
				.getUrlEncoder()
				.encodeToString(encUserName.getBytes()));
		email_link.append("/");
		email_link.append(
				Base64
				.getUrlEncoder()
				.encodeToString(encEmail.getBytes()));
		
		StringBuilder email_message = new StringBuilder();
		email_message.append("<h3>회원가입을 환영합니다</h3><br/>");
		email_message.append("<p>회원가입절차를 마무리하려면 ");
		email_message.append("Email 인증을 하여야 합니다<br/>");
		email_message.append("<p><a href='%s'>Email 인증</a>");
		email_message.append(" 링크를 클릭하여 주세요");
		
		String send_message 
			= String.format(email_message.toString(), 
						email_link.toString()				
		);
		return send_message;
		
	}
	
	
	
	
}
