package com.study.study.settings.validator;

import com.study.study.settings.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

//다른 곳에서 사용안하니까 bean으로 안만들고 new할거
public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value", "입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}
