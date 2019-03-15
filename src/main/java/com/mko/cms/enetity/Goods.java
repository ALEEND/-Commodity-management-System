package com.mko.cms.enetity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;


/**
 * @author Yuxz
 * @date 2019-03-13 18:08
 */
@Getter
@Setter
@Table(name="goods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer goodsId;
    private String goodsName;
    private String goodsDescribe;
    private String goodsPicture;
    private Integer goodsPrice;
    private Integer catalogId;
    private Integer goodsState;
    private Date ggmtCreate;
    private Date ggmtModifeid;

}
