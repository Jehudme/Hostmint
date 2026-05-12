package com.hostmint.app.web.rest;

import static com.hostmint.app.domain.ProjectAsserts.*;
import static com.hostmint.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostmint.app.IntegrationTest;
import com.hostmint.app.domain.Project;
import com.hostmint.app.domain.User;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.ProjectService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PROJECT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_KEY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/projects/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Autowired
    private ProjectMapper projectMapper;

    @Mock
    private ProjectService projectServiceMock;

    @Autowired
    private ProjectSearchRepository projectSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMockMvc;

    private Project project;

    private Project insertedProject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity(EntityManager em) {
        Project project = new Project()
            .name(DEFAULT_NAME)
            .projectKey(DEFAULT_PROJECT_KEY)
            .deleted(DEFAULT_DELETED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        project.setOwner(user);
        return project;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createUpdatedEntity(EntityManager em) {
        Project updatedProject = new Project()
            .name(UPDATED_NAME)
            .projectKey(UPDATED_PROJECT_KEY)
            .deleted(UPDATED_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedProject.setOwner(user);
        return updatedProject;
    }

    @BeforeEach
    void initTest() {
        project = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProject != null) {
            projectRepository.delete(insertedProject);
            projectSearchRepository.delete(insertedProject);
            insertedProject = null;
        }
    }

    @Test
    @Transactional
    void createProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        var returnedProjectDTO = om.readValue(
            restProjectMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectDTO.class
        );

        // Validate the Project in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProject = projectMapper.toEntity(returnedProjectDTO);
        assertProjectUpdatableFieldsEquals(returnedProject, getPersistedProject(returnedProject));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProject = returnedProject;
    }

    @Test
    @Transactional
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // set the field null
        project.setName(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProjectKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        // set the field null
        project.setProjectKey(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        restProjectMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProjects() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].projectKey").value(hasItem(DEFAULT_PROJECT_KEY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectsWithEagerRelationshipsIsEnabled() throws Exception {
        when(projectServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(projectServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(projectServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProjectMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(projectRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get the project
        restProjectMockMvc
            .perform(get(ENTITY_API_URL_ID, project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.projectKey").value(DEFAULT_PROJECT_KEY))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getProjectsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        Long id = project.getId();

        defaultProjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where name equals to
        defaultProjectFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where name in
        defaultProjectFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where name is not null
        defaultProjectFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where name contains
        defaultProjectFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where name does not contain
        defaultProjectFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectKeyIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where projectKey equals to
        defaultProjectFiltering("projectKey.equals=" + DEFAULT_PROJECT_KEY, "projectKey.equals=" + UPDATED_PROJECT_KEY);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectKeyIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where projectKey in
        defaultProjectFiltering("projectKey.in=" + DEFAULT_PROJECT_KEY + "," + UPDATED_PROJECT_KEY, "projectKey.in=" + UPDATED_PROJECT_KEY);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectKeyIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where projectKey is not null
        defaultProjectFiltering("projectKey.specified=true", "projectKey.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByProjectKeyContainsSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where projectKey contains
        defaultProjectFiltering("projectKey.contains=" + DEFAULT_PROJECT_KEY, "projectKey.contains=" + UPDATED_PROJECT_KEY);
    }

    @Test
    @Transactional
    void getAllProjectsByProjectKeyNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where projectKey does not contain
        defaultProjectFiltering("projectKey.doesNotContain=" + UPDATED_PROJECT_KEY, "projectKey.doesNotContain=" + DEFAULT_PROJECT_KEY);
    }

    @Test
    @Transactional
    void getAllProjectsByDeletedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where deleted equals to
        defaultProjectFiltering("deleted.equals=" + DEFAULT_DELETED, "deleted.equals=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllProjectsByDeletedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where deleted in
        defaultProjectFiltering("deleted.in=" + DEFAULT_DELETED + "," + UPDATED_DELETED, "deleted.in=" + UPDATED_DELETED);
    }

    @Test
    @Transactional
    void getAllProjectsByDeletedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where deleted is not null
        defaultProjectFiltering("deleted.specified=true", "deleted.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where createdAt equals to
        defaultProjectFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where createdAt in
        defaultProjectFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllProjectsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where createdAt is not null
        defaultProjectFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where updatedAt equals to
        defaultProjectFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProjectsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where updatedAt in
        defaultProjectFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllProjectsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        // Get all the projectList where updatedAt is not null
        defaultProjectFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectsByOwnerIsEqualToSomething() throws Exception {
        User owner;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            projectRepository.saveAndFlush(project);
            owner = UserResourceIT.createEntity();
        } else {
            owner = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(owner);
        em.flush();
        project.setOwner(owner);
        projectRepository.saveAndFlush(project);
        Long ownerId = owner.getId();
        // Get all the projectList where owner equals to ownerId
        defaultProjectShouldBeFound("ownerId.equals=" + ownerId);

        // Get all the projectList where owner equals to (ownerId + 1)
        defaultProjectShouldNotBeFound("ownerId.equals=" + (ownerId + 1));
    }

    private void defaultProjectFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProjectShouldBeFound(shouldBeFound);
        defaultProjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectShouldBeFound(String filter) throws Exception {
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].projectKey").value(hasItem(DEFAULT_PROJECT_KEY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectShouldNotBeFound(String filter) throws Exception {
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProject() throws Exception {
        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSearchRepository.save(project);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject);
        updatedProject
            .name(UPDATED_NAME)
            .projectKey(UPDATED_PROJECT_KEY)
            .deleted(UPDATED_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectToMatchAllProperties(updatedProject);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Project> projectSearchList = Streamable.of(projectSearchRepository.findAll()).toList();
                Project testProjectSearch = projectSearchList.get(searchDatabaseSizeAfter - 1);

                assertProjectAllPropertiesEquals(testProjectSearch, updatedProject);
            });
    }

    @Test
    @Transactional
    void putNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .projectKey(UPDATED_PROJECT_KEY)
            .deleted(UPDATED_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProject, project), getPersistedProject(project));
    }

    @Test
    @Transactional
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .name(UPDATED_NAME)
            .projectKey(UPDATED_PROJECT_KEY)
            .deleted(UPDATED_DELETED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProject.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProject))
            )
            .andExpect(status().isOk());

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(partialUpdatedProject, getPersistedProject(partialUpdatedProject));
    }

    @Test
    @Transactional
    void patchNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(projectDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);
        projectRepository.save(project);
        projectSearchRepository.save(project);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the project
        restProjectMockMvc
            .perform(delete(ENTITY_API_URL_ID, project.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(projectSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.saveAndFlush(project);
        projectSearchRepository.save(project);

        // Search the project
        restProjectMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].projectKey").value(hasItem(DEFAULT_PROJECT_KEY)))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return projectRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Project getPersistedProject(Project project) {
        return projectRepository.findById(project.getId()).orElseThrow();
    }

    protected void assertPersistedProjectToMatchAllProperties(Project expectedProject) {
        assertProjectAllPropertiesEquals(expectedProject, getPersistedProject(expectedProject));
    }

    protected void assertPersistedProjectToMatchUpdatableProperties(Project expectedProject) {
        assertProjectAllUpdatablePropertiesEquals(expectedProject, getPersistedProject(expectedProject));
    }
}
