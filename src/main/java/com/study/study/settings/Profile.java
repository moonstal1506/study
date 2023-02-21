package com.study.study.settings;

import com.study.study.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor//디폴트 생성자 필요, 없으면 널포인트
public class Profile {
    private String bio;

    private String url;

    private String occupation;

    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
