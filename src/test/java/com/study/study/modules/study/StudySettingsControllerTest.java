package com.study.study.modules.study;

import com.study.study.infra.MockMvcTest;
import com.study.study.modules.account.Account;
import com.study.study.modules.account.AccountFactory;
import com.study.study.modules.account.AccountRepository;
import com.study.study.modules.account.WithAccount;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudySettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudyFactory studyFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    StudyRepository studyRepository;

    @Test
    @WithAccount("msj")
    @DisplayName("스터디 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
    void updateDescriptionForm_fail() throws Exception {
        Account crystal = accountFactory.createAccount("crystal");
        Study study = studyFactory.createStudy("test-study", crystal);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    @WithAccount("msj")
    @DisplayName("스터디 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account msj = accountRepository.findByNickname("msj");
        Study study = studyFactory.createStudy("test-study", msj);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("msj")
    @DisplayName("스터디 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account msj = accountRepository.findByNickname("msj");
        Study study = studyFactory.createStudy("test-study", msj);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "short description")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("msj")
    @DisplayName("스터디 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account msj = accountRepository.findByNickname("msj");
        Study study = studyFactory.createStudy("test-study", msj);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                        .param("shortDescription", "")
                        .param("fullDescription", "full description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

}
