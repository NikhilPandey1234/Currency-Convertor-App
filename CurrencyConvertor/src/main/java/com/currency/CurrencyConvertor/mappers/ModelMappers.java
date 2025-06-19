package com.currency.CurrencyConvertor.mappers;

import com.currency.CurrencyConvertor.dto.ConversionHistoryDTO;
import com.currency.CurrencyConvertor.dto.CurrencyCacheDTO;
import com.currency.CurrencyConvertor.dto.RoleDTO;
import com.currency.CurrencyConvertor.dto.UserDTO;
import com.currency.CurrencyConvertor.entity.ConversionHistory;
import com.currency.CurrencyConvertor.entity.CurrencyCache;
import com.currency.CurrencyConvertor.entity.RoleEntity;
import com.currency.CurrencyConvertor.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModelMappers {

    @Autowired
    private ModelMapper modelMapper;

    public CurrencyCache currencyCacheDTOToCurrencyCacheEntity(CurrencyCacheDTO currencyCacheDTO) {
        return this.modelMapper.map(currencyCacheDTO, CurrencyCache.class);
    }

    public CurrencyCacheDTO currencyCacheEntityToCurrencyCacheDTO(CurrencyCache currencyCache){
        return this.modelMapper.map(currencyCache,CurrencyCacheDTO.class);
    }

    public ConversionHistory conversionHistoryDtoToConversionHistoryEntity(ConversionHistoryDTO conversionHistoryDTO){
        return this.modelMapper.map(conversionHistoryDTO, ConversionHistory.class);
    }

    public ConversionHistoryDTO conversionHistoryEntityToConversionHistoryDTO(ConversionHistory conversionHistory){
        return this.modelMapper.map(conversionHistory, ConversionHistoryDTO.class);
    }

    public UserEntity userDTOToUserEntity(UserDTO userDTO){
        return this.modelMapper.map(userDTO, UserEntity.class);
    }

    public UserDTO userEntityToUserDTO(UserEntity userEntity){
        return this.modelMapper.map(userEntity, UserDTO.class);
    }

    public RoleEntity roleDTOToRoleEntity(RoleDTO roleDTO){
        return this.modelMapper.map(roleDTO, RoleEntity.class);
    }

    public RoleDTO roleEntityToRoleDTO(RoleEntity roleEntity){
        return this.modelMapper.map(roleEntity, RoleDTO.class);
    }
}
