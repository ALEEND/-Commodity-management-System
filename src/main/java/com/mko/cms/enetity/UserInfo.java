package com.mko.cms.enetity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@ToString
@Table(name = "user")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;
    private String userName;
    private Integer sex;
    private String tel;
    private String password;
    private Integer age;
    private Integer role;
    private Integer state;
    private Date ugmtCreate;
    private Date ugmtModifeid;

}
