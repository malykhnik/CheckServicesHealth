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
import org.springframework.scheduling.annotation.Async;

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
            logger.info("sdfsdfasd1");
            SimpleMailMessage message = new SimpleMailMessage();
            logger.info("sdfsdfasd2");
            message.setTo(e.getReceiver());
            logger.info("sdfsdfasd3");
            message.setSubject("Отключение сервиса");
            logger.info("sdfsdfasd4");
            message.setText(text);
            logger.info("sdfsdfasd5");
            mailSender.send(message);
            logger.info("message sended to: {}", e.getReceiver());   
        }
    }
}
