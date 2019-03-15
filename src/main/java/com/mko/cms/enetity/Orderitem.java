package com.mko.cms.enetity;

import lombok.*;
import java.util.Date;
import javax.persistence.*;

/**
 * @author Yuxz
 * @date 2019-03-13 18:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@ToString
public class Orderitem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer orderitemId;
        private Integer goodsId;
        private  Integer quantity;
}
