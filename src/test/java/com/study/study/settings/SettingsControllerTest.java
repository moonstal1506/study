package com.study.study.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.study.WithAccount;
import com.study.study.account.AccountRepository;
import com.study.study.account.AccountService;
import com.study.study.domain.Account;
import com.study.study.domain.Tag;
import com.study.study.settings.form.TagForm;
import com.study.study.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("msj")
    @DisplayName("계정 태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("msj")
    @DisplayName("계정 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        assertTrue(accountRepository.findByNickname("msj").getTags().contains(newTag));
    }

    @WithAccount("msj")
    @DisplayName("계정 태그 삭제")
    @Test
    void removeTag() throws Exception {
        Account msj = accountRepository.findByNickname("msj");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(msj, newTag);

        assertTrue(msj.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(accountRepository.findByNickname("msj").getTags().contains(newTag));
    }

    @WithAccount("msj")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("msj")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account msj = accountRepository.findByNickname("msj");
        assertEquals(bio, msj.getBio());
    }

    @WithAccount("msj")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무나도 길게 소개를 수정하는 경우. ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account msj = accountRepository.findByNickname("msj");
        assertNull(msj.getBio());
    }

    @WithAccount("msj")
    @DisplayName("패스워드 수정 폼")
    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("msj")
    @DisplayName("패스워드 수정하기 - 입력값 정상")
    @Test
    void updatePassword_success() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account msj = accountRepository.findByNickname("msj");
        assertTrue(passwordEncoder.matches("12345678", msj.getPassword()));
    }

    @WithAccount("msj")
    @DisplayName("패스워드 수정하기 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_fail() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12345678")
                        .param("newPasswordConfirm", "11111111")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }
}