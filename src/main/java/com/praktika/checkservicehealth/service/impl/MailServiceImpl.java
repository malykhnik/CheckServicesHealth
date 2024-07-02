package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.entity.Email;
import com.praktika.checkservicehealth.repository.EmailRepo;
import com.praktika.checkservicehealth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final EmailRepo emailRepo;
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Async
    @Override
    public void sendMail(String text) {
        List<Email> emails = emailRepo.findAll();
        for (Email e : emails) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(e.getReceiver());
            message.setSubject("Отключение сервиса");
            message.setText(text);
            mailSender.send(message);
            logger.info("message sended to: {}", e.getReceiver());   
        }
    }
}
