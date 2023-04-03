package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                    //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;              //상품명

    @Column(nullable = false)
    private int price;                  //가격

    @Column(nullable = false)
    private int stockNumber;            //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;          //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품 판매 상태

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //상품 재고수 감소
    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    //상품 재고수 증가
    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImgs = new ArrayList<>();

    public void addItemImg(ItemImg itemImg) {
        itemImg.setItem(this);
        itemImgs.add(itemImg);
    }

    public void removeItemImg(ItemImg itemImg) {
        itemImg.setItem(null);
        itemImgs.remove(itemImg);
    }

    public List<ItemImg> getItemImgs() {
        return Collections.unmodifiableList(itemImgs);
    }
}
