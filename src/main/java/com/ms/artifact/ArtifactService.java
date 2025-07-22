package com.ms.artifact;

import com.ms.artifact.dto.ArtifactDto;
import com.ms.artifact.utils.IdWorker;
import com.ms.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

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

    public Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable) {
        Specification<Artifact> spec = Specification.where(null);

        if (StringUtils.hasLength(searchCriteria.get("id"))) {
            spec = spec.and(ArtifactSpecs.hasId(searchCriteria.get("id")));
        }

        if (StringUtils.hasLength(searchCriteria.get("name"))) {
            spec = spec.and(ArtifactSpecs.containsName(searchCriteria.get("name")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(ArtifactSpecs.containsDescription(searchCriteria.get("description")));
        }

        if (StringUtils.hasLength(searchCriteria.get("ownerName"))) {
            spec = spec.and(ArtifactSpecs.hasOwnerName(searchCriteria.get("ownerName")));
        }

        return this.artifactRepository.findAll(spec, pageable);
    }
}
