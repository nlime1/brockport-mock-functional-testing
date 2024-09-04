package unittests.servicetests;

import com.petstore.PetEntity;
import com.petstoreservices.exceptions.PetDataStoreException;
import com.petstoreservices.repository.IPetRepository;
import com.petstoreservices.repository.PetRepository;
import com.petstoreservices.service.PetInventoryService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class PetStoreServiceTests {
    @Test
    void getAllPets() throws PetDataStoreException {
        //given
        IPetRepository petRepo = new PetRepositoryStub();
        PetInventoryService pis = new PetInventoryService(petRepo);

        //when
        List<PetEntity> pets = pis.getInventory();

        //then
        assertThat(pets.size(), is(4));
    }

    @Test
    void getEmptyPet() throws PetDataStoreException
    {
        PetRepository petRepo = mock(PetRepository.class);
        PetInventoryService pis = new PetInventoryService(petRepo);
        given(pis.getInventory()).willReturn(Collections.emptyList());

        //when
        List<PetEntity> pets = pis.getInventory();

        //then
        assertThat(pets.size(), is(0));

        verify(petRepo, times(1)).getPetInventory(); //how many times inventory was called
    }
}
