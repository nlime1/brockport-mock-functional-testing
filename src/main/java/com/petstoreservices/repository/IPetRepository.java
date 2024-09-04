package com.petstoreservices.repository;

import com.petstore.PetEntity;
import com.petstore.animals.attributes.PetType;
import com.petstore.exceptions.DuplicatePetStoreRecordException;
import com.petstore.exceptions.PetNotFoundSaleException;
import com.petstore.exceptions.PetTypeNotSupportedException;
import com.petstoreservices.exceptions.PetDataStoreException;
import com.petstoreservices.exceptions.PetInventoryFileNotCreatedException;
import com.petstoreservices.exceptions.UpdatePetException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IPetRepository {
    public List<PetEntity> getPetInventory() throws PetDataStoreException;
    public PetEntity createPetEntity(PetEntity petEntity, List<PetEntity> sortedPets) throws
            PetInventoryFileNotCreatedException, PetDataStoreException;
    public PetEntity updatePetEntity(@NotNull PetEntity petEntity, PetEntity updatedPetItem) throws
            PetTypeNotSupportedException, UpdatePetException, PetDataStoreException,
            PetInventoryFileNotCreatedException;
    public PetEntity removeEntity(PetEntity petEntity) throws
            PetNotFoundSaleException, PetInventoryFileNotCreatedException, PetDataStoreException;
    public PetEntity findPetByPetTypeAndPetId(PetType petType, int petId) throws PetNotFoundSaleException,
            DuplicatePetStoreRecordException, PetDataStoreException;
}
