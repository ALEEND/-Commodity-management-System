package com.mko.cms.enetity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Yuxz
 * @date 2019-03-21 14:14
 */
@Getter
@Setter
@Table(name="orderitem")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Orderitem  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderitemId;
    private Integer goodsId;
    private Integer orderId;
    private Integer quantity;
    private  Double subTotal;
    private Date gmtCreate;

}
