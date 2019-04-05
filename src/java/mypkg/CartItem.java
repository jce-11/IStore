/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mypkg;
/**
 *
 * @author jasonevans
 */
public class CartItem {
    private String category;
    private int itemID;
    private String itemName;
    private String itemDsc;
    private float itemPrice;
    private int qtyOrdered;
    
    public CartItem(String category, int itemID, String itemName, 
            String itemDsc, float itemPrice, int qtyOrdered){
        this.category = category;
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemDsc = itemDsc;
        this.itemPrice = itemPrice;
        this.qtyOrdered = qtyOrdered;
    }
    public int getItemID(){
        return itemID;
    }
    public String getCategory(){
        return category;
    }
    public String getItemName(){
        return itemName;
    }
    public String getItemDsc(){
        return itemDsc;
    }
    public float getItemPrice(){
        return itemPrice;
    }
    public int getQtyOrdered(){
        return qtyOrdered;
    }
    public void setQtyOrdered(int qtyOrdered){
        this.qtyOrdered = qtyOrdered;
    }
    
}
