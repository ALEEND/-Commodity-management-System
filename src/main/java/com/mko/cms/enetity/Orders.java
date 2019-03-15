package com.mko.cms.enetity;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

/**
 * @author Yuxz
 * @date 2019-03-13 18:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@ToString
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer orderId;
    private  Integer userID;
    private Date orderdate;
    private String orderNumber;
    private Integer orderState;
    private Integer goodsId;
    private Double totalPrice;

}
