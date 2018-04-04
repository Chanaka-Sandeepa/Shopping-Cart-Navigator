package com.example.root.shoppingassistance.Controller;

import android.util.Log;

import com.example.root.shoppingassistance.Model.Attribute;
import com.example.root.shoppingassistance.Model.Item;
import com.example.root.shoppingassistance.Model.Shop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ShoppingAssistanceController {
    private String currentItem;
    private ArrayList<String> shoopingCart = new ArrayList<String>();

    List<Item> items = new ArrayList<Item>();
    List<Item> categoryItems = new ArrayList<Item>();
    int i;

    public ShoppingAssistanceController() throws ParseException {
        getItems();
        categoryItems = new ArrayList<Item>();
    }



    public List<Item> getItems() throws ParseException {


            Item item1 = new Item();
            item1.setCategory("shirt");
            item1.setName("formal");
            item1.setPrice(3000);
            String sDate = "2018-01-31";
            Date date = new SimpleDateFormat("yyyy-mm-dd").parse(sDate);
            item1.setDateOfPurchace(date);
            Shop shop = new Shop();
            shop.setShopName("Nolimit");
            shop.setAddress("752,Galle Rd,Colombo 06");
            item1.setShop(shop);
            Attribute attribute = new Attribute("type ? formal or casual", "formal", 1);
            item1.addAttribute(attribute);
            attribute = new Attribute("size", "medium", 2);
            item1.addAttribute(attribute);

            items.add(item1);

            Item item2 = new Item();
            item2.setCategory("shirt");
            item2.setName("casual");
            item2.setPrice(3000);
            item2.setDateOfPurchace(date);
            item2.setShop(shop);
            attribute = new Attribute("type ? formal or casual", "casual", 1);
            item2.addAttribute(attribute);
            attribute = new Attribute("size", "large", 2);
            item2.addAttribute(attribute);

            items.add(item2);

            Item item3 = new Item();
            item3.setCategory("mobile");
            item3.setName("smart phone");
            item3.setPrice(3000);
            item3.setDateOfPurchace(date);
            item3.setShop(shop);
            attribute = new Attribute("kind ? smart phone or normal phone", "smartphone", 1);
            item3.addAttribute(attribute);
            attribute = new Attribute("brand", "apple", 2);
            item3.addAttribute(attribute);
            attribute = new Attribute("model", "iphone 6", 3);
            item3.addAttribute(attribute);

            items.add(item3);

            print("list created");
            return items;


    }

    public List<Item> getItemsOfCategory(String s){


        for(i=0;i<items.size();i++){
            if(items.get(i).getCategory().equals(s)){
                categoryItems.add(items.get(i));
            }
        }
        return categoryItems;
    }

    public String selectBestResponse(ArrayList<String> result){
        for (String r : result){
            for(Item i : items){
                if(r.equals(i.getCategory())){
                    currentItem = r;
                    return r;
                }
            }
        }
        return result.get(0);
    }

    public String nextQuestion(int index,String lastResponse){

        String nextQ = "invalid";
        if (categoryItems==null||categoryItems.isEmpty()){
            print(nextQ+" con");
            return nextQ;
        }
        else{
            print(categoryItems.get(0).getCategory());
            print(String.valueOf(index)+" con");
            for (i=0;i<categoryItems.size();i++){
                if(categoryItems.get(i).getAttributes().get(index-1).getAttributeName().equals(lastResponse)) {
                    if (index < categoryItems.get(i).getAttributes().size()) {
                        return categoryItems.get(i).getAttributes().get(index).getAttributeType();
                    }
                    else{
                        return "success";
                    }
                }
            }
            print(nextQ+" con");
            return nextQ;

        }
    }

    public String getFirstQ(){
        print(String.valueOf(categoryItems.size()));
        return categoryItems.get(0).getAttributes().get(0).getAttributeType();
    }

    private void print(String s){
        Log.e("message",s);
    }

    public void addToCart(){
        shoopingCart.add(currentItem);
    }

    public String getShoppingCart(){
        String shoppingList = "";
        for(String s :shoopingCart){
            shoppingList.concat(s);
        }
        return shoppingList;
    }

}
