package com.study.study.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account crystal = new Account();
        crystal.setNickname(nickname);
        crystal.setEmail(nickname + "@email.com");
        accountRepository.save(crystal);
        return crystal;
    }

}
