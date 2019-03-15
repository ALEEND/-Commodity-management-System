package com.mko.cms.enetity;

import lombok.*;

import javax.persistence.*;
import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @author Yuxz
 * @date 2019-03-14 11:35
 */
@Getter
@Setter
@Table(name="cart")
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;
    private  Integer userID;
    private  Integer goodsId;
    private  Integer quantity;
    private double allPrice;
    private  Date cgmtModifeid;
    private Date cartDate;
}
