package com.ms.artifact;

import com.ms.artifact.utils.IdWorker;
import com.ms.system.exception.ObjectNotFoundException;
import com.ms.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");

        artifacts = new ArrayList<>();
        artifacts.add(a1);
        artifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess()
    {
       /* "id": "1250808601744904192",
            "name": "Invisibility Cloak",
            "description": "An invisibility cloak is used to make the wearer invisible.",
            "imageUrl": "ImageUrl",
            */
        Artifact a = new Artifact();
        a.setId("1250808601744904192");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));

        Artifact returnedArtifact = artifactService.findById("1250808601744904192");

        assertThat(returnedArtifact.getId()).isEqualTo(a.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(a.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(a.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(a.getImageUrl());
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindByIdNotFound()
    {
        given(artifactRepository.findById(any(String.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1250808601744904192");
        });

        assertThat(thrown)
                    .isInstanceOf(ObjectNotFoundException.class)
                    .hasMessage("Could not find artifact with Id 1250808601744904192 :(");
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

    @Test
    void testFindAllSuccess()
    {
        given(artifactRepository.findAll()).willReturn(artifacts);
        List<Artifact> actualArtifacts = artifactService.findAll();

        assertThat(actualArtifacts.size()).isEqualTo(artifacts.size());
        verify(artifactRepository,times(1)).findAll();
    }

    @Test
    void testSaveSuccess()
    {
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description...");
        newArtifact.setImageUrl("ImageUrl...");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        Artifact savedArtifact = artifactService.save(newArtifact);

        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository,times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess()
    {
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility Cloak");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        Artifact updatedArtifact = artifactService.update("1250808601744904192",update);

        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(artifactRepository,times(1)).findById("1250808601744904192");
        verify(artifactRepository,times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound()
    {
        Artifact update = new Artifact();
        update.setName("Invisibility Cloak");
        update.setDescription("A new description");
        update.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
           artifactService.update("1250808601744904192",update);
        });

        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }

    @Test
    void testDeleteSuccess()
    {
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifact.setImageUrl("ImageUrl");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));

        doNothing().when(artifactRepository).deleteById("1250808601744904192");

        artifactService.delete("1250808601744904192");

        verify(artifactRepository,times(1)).deleteById("1250808601744904192");
    }

    @Test
    void testDeleteNotFound()
    {
        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
           artifactService.delete("1250808601744904192");
        });
        verify(artifactRepository,times(1)).findById("1250808601744904192");
    }
}