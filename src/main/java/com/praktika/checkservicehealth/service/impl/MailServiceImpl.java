package com.praktika.checkservicehealth.service.impl;

import com.praktika.checkservicehealth.entity.Email;
import com.praktika.checkservicehealth.repository.EmailRepo;
import com.praktika.checkservicehealth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final EmailRepo emailRepo;
    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String text) {
        List<Email> emails = emailRepo.findAll();

        Runnable runnable = new Runnable() {
            public void run() {
                for(Email e : emails) {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(e.getReceiver());
                    message.setSubject("Отключение сервиса");
                    message.setText(text);
                    mailSender.send(message);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
