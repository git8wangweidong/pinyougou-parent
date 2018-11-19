package com.pinyougou.shop.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellers.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        TbSeller seller = sellerService.findOne(username);
        if (seller!=null){
            if (seller.getStatus().equals("1")){
                return new User(seller.getSellerId(),seller.getPassword(),authorities);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
