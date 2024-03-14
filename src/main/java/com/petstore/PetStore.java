package com.petstore;


import java.util.ArrayList;

import java.util.List;

/**
 * Pet Store which holds and controls the pet inventory.
 * It currently supports add, removing, and updating inventory
 */
public class PetStore
{
    private List<PetEntity> petInventoryList;

    private final List<PetEntity> petsSold; //not implemented

    public PetStore()
    {
        petInventoryList = new ArrayList<PetEntity>();
        petsSold = new ArrayList<PetEntity>();

    }

    /**
     * Constructor to initialize pet for sale list only
     * @param petInventoryList - list of pet items the pet store has in inventory for sale
     */
    public PetStore(List<PetEntity> petInventoryList)
    {
        petsSold = new ArrayList<PetEntity>();
        this.petInventoryList = petInventoryList;
    }

    /*
     * Remove pet items from the petsForSale list
     * @param soldPet - pet which is being removed from inventory
     * @return - return the pet which was removed
     * @throws DuplicatePetStoreRecordException - if duplicate records found by pet type cannot delete
     * @throws PetNotFoundSaleException - if pet not found cannot sell the pet

    public PetEntity removePetItemFromInventory(PetEntity soldPet)
            throws DuplicatePetStoreRecordException, PetNotFoundSaleException{
        PetEntity tmpPet;
        if (soldPet.getPetId()==0)
        {
            throw new PetNotFoundSaleException("The Pet is not part of the pet store!!");
        }
        else
        {
            tmpPet = this.identifyPetFromInventory(soldPet);
            this.removePetFromInventoryByPetId(soldPet.getPetType(), soldPet.getPetId());
        }
        return tmpPet;
    }*/

    /*
     * Add item to the inventory list
     * @param pet {@link PetEntity} to be added to the inventory

    public void addPetInventoryItem(PetEntity pet)
    {

        this.petInventoryList.add(pet);
    } */

    /*
     * Remove the Pet from the pet store by id and type of pet
     * @param petType - the pet type
     * @param petStoreId - unique pet id by pet type

    @Deprecated
    private void removePetFromInventoryByPetId(PetType petType, int petStoreId) {

        List<PetEntity> otherPets = this.petInventoryList.stream()
                .filter(p -> (p.getPetType() != petType))
                .collect(Collectors.toList());
        List<PetEntity> listForFiltering =  this.petInventoryList.stream()
                    .filter(p -> ((p.getPetType() == petType)
                            && (p.getPetId() != petStoreId)))
                    .collect(Collectors.toList());

       listForFiltering.addAll(otherPets);//add other lists
       this.petInventoryList = listForFiltering;
    }*/

    /*
     * Identify the Dog to remove from the inventory list
     * @param soldDog the {@link DogEntity} that will be sold
     * @return the {@link DogEntity} that was sold
     * @throws DuplicatePetStoreRecordException if there is duplicate dog record

    private DogEntity identifySoldDogFromInventory(DogEntity soldDog) throws DuplicatePetStoreRecordException
    {
        List<PetEntity> dogPets = this.petInventoryList.stream()
                .filter(p -> ((p instanceof DogEntity)
                        && (p.getPetId() == soldDog.getPetId())))
                .collect(Collectors.toList());

        if (dogPets.isEmpty())
        {
            return null;
        }
        else if (dogPets.size()==1)
        {
            return (DogEntity) dogPets.get(0);
        }
        else {
            throw new DuplicatePetStoreRecordException ("Duplicate Dog record store id [" + soldDog.getPetId() + "]");
        }
    } */

    /*
     * Identify the cat which was sold from the inventory list

     * @return the {@link CatEntity} that was sold
     * @throws DuplicatePetStoreRecordException if there is duplicate cat record

    private PetEntity identifyPetFromInventory(PetEntity petItem) throws DuplicatePetStoreRecordException
    {
        List<PetEntity> petItems = this.petInventoryList.stream()
                .filter(p -> ((p.getPetType() == petItem.getPetType())
                        && (p.getPetId() == petItem.getPetId())))
                .collect(Collectors.toList());

        if (petItems.isEmpty())
        {
            return null;
        }
        else if (petItems.size()==1)
        {
            return  petItems.get(0);
        }
        else {
            throw new DuplicatePetStoreRecordException ("Duplicate Cat record store id [" +
                    petItems.get(0).getPetId() + "]");
        }
    }*/

    @Deprecated
    public List<PetEntity> getPetsForSale()
    {
        return petInventoryList;
    }
    @Deprecated
    public void loadPetInventory(List<PetEntity> newPetsListForSale)
    {
        petInventoryList = newPetsListForSale;
    }

}
