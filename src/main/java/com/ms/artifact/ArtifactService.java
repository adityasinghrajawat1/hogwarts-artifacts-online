package com.ms.artifact;

import com.ms.artifact.dto.ArtifactDto;
import com.ms.artifact.utils.IdWorker;
import com.ms.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService
{
    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId)
    {
        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
    }

    public List<Artifact> findAll()
    {
        return artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact)
    {
        newArtifact.setId(idWorker.nextId()+"");
        return artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update)
    {
       return artifactRepository.findById(artifactId)
                .map(obj -> {
                    obj.setName(update.getName());
                    obj.setDescription(update.getDescription());
                    obj.setImageUrl(update.getImageUrl());
                    return artifactRepository.save(obj);
                })
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
    }

    public void delete(String artifactId)
    {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact",artifactId));
        artifactRepository.deleteById(artifactId);
    }

    public Page<Artifact> findAll(Pageable pageable) {
        return artifactRepository.findAll(pageable);
    }
}
