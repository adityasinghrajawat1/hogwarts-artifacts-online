package com.ms.artifact;

import com.ms.artifact.dto.ArtifactDto;
import com.ms.artifact.utils.IdWorker;
import jakarta.transaction.Transactional;
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
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
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
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public void delete(String artifactId)
    {
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        artifactRepository.deleteById(artifactId);
    }
}
