package com.mock.interview.user.presentation.dto;

import com.mock.interview.category.presentation.dto.JobCategorySelectedIds;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountForm {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private AccountDto account = new AccountDto();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private JobCategorySelectedIds categories = new JobCategorySelectedIds();

    public String getPassword() {return account.getPassword();}
    public String getUsername() {return account.getUsername();}
    public Long getCategoryId() {return categories.getCategoryId();}
    public Long getPositionId() {return categories.getPositionId();}

    public void setUsername(String username) {account.setUsername(username);}
    public void setPassword(String password) {account.setPassword(password);}
    public void setCategoryId(Long categoryId) {categories.setCategoryId(categoryId);}
    public void setPositionId(Long positionId) {this.categories.setPositionId(positionId);}
}