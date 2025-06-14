package org.jnjeaaaat.snms.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.jnjeaaaat.snms.domain.auth.dto.request.SmsSendRequest;
import org.jnjeaaaat.snms.domain.auth.entity.RedisSms;
import org.jnjeaaaat.snms.domain.auth.exception.AuthException;
import org.jnjeaaaat.snms.domain.auth.repository.RedisSmsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.SMS_SEND_ERROR;
import static org.jnjeaaaat.snms.global.util.LogUtil.logError;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoolSmsService {

    @Value("${coolsms.from}")
    String fromNum;

    private final DefaultMessageService messageService;
    private final SecureRandom secureRandom;
    private final RedisSmsRepository redisSmsRepository;

    public void sendSms(HttpServletRequest request, SmsSendRequest smsSendRequest) {
        log.info("인증번호 전송 요청 : {}", smsSendRequest.phoneNum());

        String authCode = generateSecureAuthCode();
        redisSmsRepository.save(new RedisSms(smsSendRequest.phoneNum(), authCode));

        Message message = new Message();

        message.setFrom(fromNum);
        message.setTo(smsSendRequest.phoneNum());
        message.setText("[SNMS] 본인확인 인증번호 [" + authCode + "] 입니다.");
        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception exception) {
            logError(request, exception);

            throw new AuthException(SMS_SEND_ERROR);
        }

    }

    private String generateSecureAuthCode() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}
