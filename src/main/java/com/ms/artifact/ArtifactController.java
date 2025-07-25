package com.ms.artifact;

import com.ms.artifact.converter.ArtifactDtoToArtifactConverter;
import com.ms.artifact.converter.ArtifactToArtifactDtoConverter;
import com.ms.artifact.dto.ArtifactDto;
import com.ms.system.Result;
import com.ms.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController
{
    private final ArtifactService artifactService;

    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    public ArtifactController(ArtifactService artifactService,
                              ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                              ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId)
    {
        Artifact foundArtifact = artifactService.findById(artifactId);
        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(foundArtifact);
        return new Result(true, StatusCode.SUCCESS,"Find One Success",artifactDto);
    }

    @GetMapping
    public Result findAllArtifacts(Pageable pageable)
    {
        Page<Artifact> artifactPage = artifactService.findAll(pageable);

        Page<ArtifactDto> artifactDtoPage = artifactPage
                                        .map( i -> artifactToArtifactDtoConverter.convert(i));
        return new Result(true,StatusCode.SUCCESS,"Find All Success",artifactDtoPage);
    }

    @PostMapping
    public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto)
    {
        Artifact newArtifact = artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact savedArtifact = artifactService.save(newArtifact);
        ArtifactDto savedArtifactDto = artifactToArtifactDtoConverter.convert(savedArtifact);
        return new Result(true,StatusCode.SUCCESS,"Add Success",savedArtifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable String artifactId,@Valid @RequestBody ArtifactDto artifactDto)
    {
        Artifact update =  artifactDtoToArtifactConverter.convert(artifactDto);
        Artifact updatedArtifact = artifactService.update(artifactId,update);
        ArtifactDto updatedArtifactDto = artifactToArtifactDtoConverter.convert(updatedArtifact);
        return new Result(true,StatusCode.SUCCESS,"Update Success",updatedArtifactDto);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId)
    {
        artifactService.delete(artifactId);
        return new Result(true,StatusCode.SUCCESS,"Delete Success");
    }

    @PostMapping("/search")
    public Result findArtifactsByCriteria(@RequestBody Map<String, String> searchCriteria, Pageable pageable) {
        Page<Artifact> artifactPage = this.artifactService.findByCriteria(searchCriteria, pageable);
        Page<ArtifactDto> artifactDtoPage = artifactPage.map(this.artifactToArtifactDtoConverter::convert);
        return new Result(true, StatusCode.SUCCESS, "Search Success", artifactDtoPage);
    }
}
