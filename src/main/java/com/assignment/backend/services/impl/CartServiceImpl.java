package com.assignment.backend.services.impl;

import java.util.List;

import org.hibernate.boot.jaxb.internal.stax.LocalXmlResourceResolver.DtdMapping;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.assignment.backend.data.entities.Account;
import com.assignment.backend.data.entities.Cart;
import com.assignment.backend.data.entities.CartItem;
import com.assignment.backend.data.repositories.AccountRepository;
import com.assignment.backend.data.repositories.CartItemRepository;
import com.assignment.backend.data.repositories.CartRepository;
import com.assignment.backend.dto.request.CartItemCreateDto;
import com.assignment.backend.dto.response.CartItemResponseDto;
import com.assignment.backend.dto.response.CartResponseDto;
import com.assignment.backend.dto.response.SuccessResponse;
import com.assignment.backend.exceptions.ResourceNotFoundException;
import com.assignment.backend.services.CartService;
import com.assignment.backend.utils.Utils;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartResponseDto getCartByAccount(int accId) {
        Account acc = this.accountRepository.findById(accId)
                .orElseThrow(() -> new ResourceNotFoundException(Utils.NO_ACCOUNT));

        Cart cart = this.cartRepository.findByAccountAndActive(acc, true);

        if (cart == null) {
            throw new ResourceNotFoundException(Utils.NO_CART);
        }
        return modelMapper.map(cart, CartResponseDto.class);
    }

    @Override
    public ResponseEntity<SuccessResponse> addProductToCart(CartItemCreateDto dto) {
        // check cart is exist
        if (!this.cartRepository.existsById(dto.getCartId())) {
            throw new ResourceNotFoundException(Utils.NO_CART);
        }

        // save
        this.cartItemRepository.save(modelMapper.map(dto, CartItem.class));

        return ResponseEntity.ok(new SuccessResponse(HttpStatus.CREATED, "Added product into cart successfully"));
    }

    @Override
    public ResponseEntity<SuccessResponse> updateQuantityProduct(CartItemCreateDto dto) {
        CartItem cartItem = this.cartItemRepository.findByCartIdAndProId(dto.getCartId(), dto.getProId());
        if (cartItem == null) {
            throw new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND);
        }

        cartItem.setQuantity(dto.getQuantity());

        this.cartItemRepository.save(cartItem);
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK, "Changed Quantity"));
    }

    @Override
    public ResponseEntity<SuccessResponse> deleteProductOutOfCart(int proId, int cartId) {
        CartItem cartItem = this.cartItemRepository.findByCartIdAndProId(cartId, proId);
        if (cartItem == null) {
            throw new ResourceNotFoundException(Utils.PRODUCT_NOT_FOUND);
        }

        this.cartItemRepository.delete(cartItem);

        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK, "Deleted product out of cart"));
    }
    
}