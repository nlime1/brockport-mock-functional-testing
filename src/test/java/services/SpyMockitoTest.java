package services;

import com.petstore.AnimalType;
import com.petstore.PetEntity;
import com.petstore.animals.CatEntity;
import com.petstore.animals.DogEntity;
import com.petstore.animals.attributes.Breed;
import com.petstore.animals.attributes.Gender;
import com.petstore.animals.attributes.PetType;
import com.petstore.animals.attributes.Skin;
import com.petstore.exceptions.PetNotFoundSaleException;
import com.petstore.exceptions.PetTypeNotSupportedException;
import com.petstoreservices.exceptions.PetDataStoreException;
import com.petstoreservices.exceptions.PetInventoryFileNotCreatedException;
import com.petstoreservices.exceptions.PetStoreAnimalTypeException;
import com.petstoreservices.exceptions.RequestBodyException;
import com.petstoreservices.repository.PetRepository;
import com.petstoreservices.service.PetInventoryService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.stream.Stream;

import static com.petstore.animals.attributes.Skin.FUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpyMockitoTest {
    @InjectMocks
    private PetInventoryService petService;

    @Mock
    private PetRepository petRepository;

    private Spy  myPets;

    private PetEntity newDogItem;




    @BeforeEach
    public void init() throws PetDataStoreException
    {
        spy(new ArrayList<PetEntity>(Arrays.asList(
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                        new BigDecimal("750.00"), 4),
                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"),1)
        )));/*) new ArrayList<PetEntity>(Arrays.asList(
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                        new BigDecimal("750.00"), 4),
                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"),1)
        ));*/
        //Mock the getPetInveontory() with results
        Mockito.lenient().doReturn(myPets).when(petRepository).getPetInventory();
    }

    @TestFactory
    @DisplayName("Spy example test")
    public Stream<DynamicTest> getInventoryTestByDog() throws PetNotFoundSaleException, PetDataStoreException
    {
        List<PetEntity> foundPetList = petService.getPetsByPetType(PetType.DOG);  //retrieve the data
        List<DynamicTest> inventoryTests = Arrays.asList(
                DynamicTest.dynamicTest("List size test",
                        ()-> assertEquals(3, foundPetList.size())),
                DynamicTest.dynamicTest("Pet item with Dog id 1",
                        ()-> assertTrue(foundPetList.stream()
                                .anyMatch(c -> c.getPetId()==1 && c.getPetType() == PetType.DOG
                                        && c.getGender() ==Gender.MALE && c.getBreed() == Breed.POODLE))),
                DynamicTest.dynamicTest("Pet item with Dog id 3",
                        ()-> assertTrue(foundPetList.stream()
                                .anyMatch(c -> c.getPetId()== 3&& c.getPetType() == PetType.DOG
                                        && c.getGender() ==Gender.MALE && c.getBreed() == Breed.MALTESE))),
                DynamicTest.dynamicTest("Pet item with Cat id 1 Not Found",
                        ()-> assertTrue(foundPetList.contains(
                                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                                    new BigDecimal("65.00"),1))==false)));

        return inventoryTests.stream();
    }
    @TestFactory
    @Order(3)
    @DisplayName("Validate Add DOG POST Test")
    public Stream<DynamicTest> postPetTest() throws RequestBodyException, PetInventoryFileNotCreatedException,
            PetStoreAnimalTypeException, PetDataStoreException, PetTypeNotSupportedException {

        newDogItem = new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GERMAN_SHEPHERD,
                new BigDecimal("225.00"), 2);
        List<PetEntity> sortedPets =
                new ArrayList<PetEntity>(
                        Arrays.asList(
                            new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                                new BigDecimal("750.00"), 3),
                            new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                                    new BigDecimal("650.00"), 1),
                            new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                                    new BigDecimal("750.00"), 4)));
        //Mockito.lenient().doReturn(newDogItem).when(this.petRepository).createPetEntity(newDogItem,sortedPets);
        doReturn(newDogItem).when(this.petRepository).createPetEntity(newDogItem, (List<PetEntity>) myPets);
       // this.petRepository.setPetRepository(myPets);
        System.out.println("Added id[" + newDogItem.getPetId() + "]");
        PetEntity aEntity = this.petService.addInventory(PetType.DOG, newDogItem);

        System.out.println("Return Entity id[" + newDogItem.getPetId() + "]");

        for(PetEntity pet : sortedPets)
        {
            System.out.println("say what[" + pet.getPetId() + "]");
        }

        List<DynamicTest> inventoryTests = Arrays.asList(
                DynamicTest.dynamicTest("Pet item with Dog id 2",
                        ()-> assertEquals(2, aEntity.getPetId())),
                DynamicTest.dynamicTest("Dog breed",
                        ()-> assertTrue(AnimalType.DOMESTIC == aEntity.getAnimalType())),
                DynamicTest.dynamicTest("Dog Gender",
                        ()-> assertTrue(Gender.FEMALE == aEntity.getGender())));
        verify(petRepository, times(1)).createPetEntity(newDogItem, (List<PetEntity>) myPets);

        return inventoryTests.stream();
    }
}
