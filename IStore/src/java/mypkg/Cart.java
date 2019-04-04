/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mypkg;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
 *
 * @author jasonevans
 */
public class Cart implements Serializable {
    private List<CartItem> cart;
    
    public Cart(){
    cart = new ArrayList<CartItem>();
    }   
    public void add(String category, int itemID, 
            String itemName, String itemDsc, float itemPrice, int qtyOrdered){
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()){
            CartItem item = iter.next();
            if (item.getItemID() == itemID){
                item.setQtyOrdered(item.getQtyOrdered() + qtyOrdered);
                return;
            }
        }
        cart.add(new CartItem(category,itemID, 
            itemName, itemDsc,itemPrice,qtyOrdered));
        
    }
    public boolean update(int id, int newQty){
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()){
            CartItem item = iter.next();
            item.setQtyOrdered(newQty);
            return true;
        }
        return false;
    }
    public void remove(int id){
        Iterator<CartItem> iter = cart.iterator();
        while (iter.hasNext()){
            CartItem item = iter.next();
            if(item.getItemID() == id){
                cart.remove(item);
                return;
            }
        }
    }
    public int size(){
        return cart.size();
    }
    public boolean isEmpty(){
        return size() == 0;
    }
    public List<CartItem> getItems(){
        return cart;
    }
    public void clear(){
        cart.clear();
    }
    
   
}
