package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.entity.Email;
import com.praktika.checkservicehealth.repository.EmailRepo;
import com.praktika.checkservicehealth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${email.from}")
    String EMAIL_FROM;

    @Override
    public void sendMail(String text) {
        logger.info("ВЫЗВАНА ФУНКЦИЯ sendMail");
        Thread thread = new Thread(() -> {
            List<Email> emails = emailRepo.findAll();
            for (Email email : emails) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(EMAIL_FROM);
                message.setTo(email.getReceiver());
                message.setSubject("Отключение сервиса");
                message.setText(text);
                try {
                    logger.info("ПОПЫТКА ОТПРАВИТЬ СООБЩЕНИЕ");
                    mailSender.send(message);
                    logger.info("Письмо отправлено успешно");
                } catch (Exception e) {
                    logger.error("Ошибка при отправке письма", e);
                }
            }
        });
        thread.start();
    }

}
