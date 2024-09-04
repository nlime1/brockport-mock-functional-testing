package unittests.servicetests;
import com.petstore.AnimalType;
import com.petstore.PetEntity;
import com.petstore.animals.CatEntity;
import com.petstore.animals.DogEntity;
import com.petstore.animals.attributes.Breed;
import com.petstore.animals.attributes.Gender;
import com.petstore.animals.attributes.PetType;
import com.petstore.animals.attributes.Skin;
import com.petstore.exceptions.DuplicatePetStoreRecordException;
import com.petstore.exceptions.PetNotFoundSaleException;
import com.petstore.exceptions.PetTypeNotSupportedException;
import com.petstoreservices.exceptions.PetDataStoreException;
import com.petstoreservices.exceptions.PetInventoryFileNotCreatedException;
import com.petstoreservices.exceptions.UpdatePetException;
import com.petstoreservices.repository.IPetRepository;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.petstore.animals.attributes.Skin.*;

public class PetRepositoryStub implements IPetRepository
{


    @Override
    public List<PetEntity> getPetInventory() throws PetDataStoreException {
        List<PetEntity> myPets = new ArrayList<PetEntity>(Arrays.asList(
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                        new BigDecimal("750.00"), 4),
                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"),1)
        ));

        return myPets;
    }
    public PetEntity createPetEntity(PetEntity petEntity, List<PetEntity> sortedPets) throws
            PetInventoryFileNotCreatedException, PetDataStoreException
    {
        PetEntity pet = new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                new BigDecimal("1750.00"), 5);
      return pet;
    }

    @Override
    public PetEntity updatePetEntity(@NotNull PetEntity petEntity, PetEntity updatedPetItem)
            throws PetTypeNotSupportedException, UpdatePetException, PetDataStoreException,
            PetInventoryFileNotCreatedException {
            if(updatedPetItem.getGender() == Gender.MALE)
            {
                updatedPetItem.setGender(Gender.FEMALE);
            }else {
                updatedPetItem.setGender(Gender.MALE);
            }
            if(updatedPetItem.getCost() == new BigDecimal("500.00"))
            {
                updatedPetItem.setCost(new BigDecimal("1250.00"));
            }else {
                updatedPetItem.setCost(new BigDecimal("500.00"));
            }
            if(updatedPetItem.getLegs() == 2)
            {
                updatedPetItem.setLegs(3);
            }else {
                updatedPetItem.setLegs(2);
            }
            if(updatedPetItem.getSkinType() == FUR)
            {
                updatedPetItem.setSkinType(UNKNOWN);
            }else {
                updatedPetItem.setSkinType(FUR);
            }
            switch(updatedPetItem.getPetType())
            {
                case CAT:
                    if(updatedPetItem.getBreed() == Breed.SPHYNX)
                    {
                        updatedPetItem.setBreed(Breed.SIAMESE);
                    }
                    else
                    {
                        updatedPetItem.setBreed(Breed.SPHYNX);
                    }
                    break;
                case DOG:
                default:
                    if(updatedPetItem.getBreed() == Breed.GREY_HOUND)
                    {
                        updatedPetItem.setBreed(Breed.MALTESE);
                    }
                    else
                    {
                        updatedPetItem.setBreed(Breed.GREY_HOUND);
                    }
                    break;
            }
        return updatedPetItem;
    }

    @Override
    public PetEntity removeEntity(PetEntity petEntity) throws PetNotFoundSaleException, PetInventoryFileNotCreatedException, PetDataStoreException {
        return petEntity;

    }

    @Override
    public PetEntity findPetByPetTypeAndPetId(PetType petType, int petId) throws PetNotFoundSaleException, DuplicatePetStoreRecordException, PetDataStoreException {
        List<PetEntity> myPets = new ArrayList<PetEntity>(Arrays.asList(
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                        new BigDecimal("750.00"), 4),
                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"),1)
        ));
        List<PetEntity> filteredPets =  this.getPetInventory().stream()
                .filter(p -> p.getPetType().equals(petType))
                .filter(id -> id.getPetId()==petId)
                .collect(Collectors.toList());

        if(filteredPets.isEmpty())
        {
            throw new PetNotFoundSaleException("0 results found for search criteria for pet id[" + petId + "] " +
                    "petType[" + petType +"] Please try again!!");
        }

        return myPets.get(0);
    }
}
