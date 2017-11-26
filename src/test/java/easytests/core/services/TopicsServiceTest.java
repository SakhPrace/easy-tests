package easytests.core.services;

import easytests.core.entities.TopicEntity;
import easytests.core.mappers.TopicsMapper;
import easytests.core.models.TopicModelInterface;
import easytests.support.TopicsSupport;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * @author lelay
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TopicsServiceTest {

    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @MockBean
    private TopicsMapper topicsMapper;

    @Autowired
    private TopicsService topicsService;

    private TopicsSupport topicsSupport = new TopicsSupport();

    private List<TopicEntity> getTopicsFixturesEntities() {
        final List<TopicEntity> topicsEntities = new ArrayList<>(2);
        topicsEntities.add(this.topicsSupport.getEntityFixtureMock(1));
        topicsEntities.add(this.topicsSupport.getEntityFixtureMock(2));
        return topicsEntities;
    }

    private List<TopicModelInterface> getTopicsFixturesModels() {
        final List<TopicModelInterface> topicsModels = new ArrayList<>(2);
        topicsModels.add(this.topicsSupport.getModelFixtureMock(1));
        topicsModels.add(this.topicsSupport.getModelFixtureMock(2));
        return topicsModels;
    }

    private void assertEquals(List<TopicModelInterface> expected, List<TopicModelInterface> actual) {
        Assert.assertEquals(expected.size(), actual.size());

        Integer i = 0;
        for(TopicModelInterface topicModel: expected) {
            this.topicsSupport.assertEquals(topicModel, actual.get(i++));
        }
    }

    @Test
    public void testFindAllPresentModel() throws Exception {
        final List<TopicEntity> topicsEntities = this.getTopicsFixturesEntities();
        when(this.topicsMapper.findAll()).thenReturn(topicsEntities);

        final List<TopicModelInterface> topicsFoundedModels = this.topicsService.findAll();

        this.assertEquals(topicsFoundedModels, this.getTopicsFixturesModels());
    }

    @Test
    public void testFindAllAbsentModel() throws Exception {
        when(this.topicsMapper.findAll()).thenReturn(new ArrayList<>(0));

        final List<TopicModelInterface> topicsFoundedModels = this.topicsService.findAll();

        Assert.assertNotNull(topicsFoundedModels);
        Assert.assertEquals(0, topicsFoundedModels.size());
    }

    @Test
    public void testFindAllWithOptions() throws Exception {
        //TODO: this test
        Assert.assertEquals(true, false);
    }

    @Test
    public void testFindPresentList() throws Exception {
        final TopicEntity topicExistentEntity = this.topicsSupport.getEntityFixtureMock(0);
        when(this.topicsMapper.find(topicExistentEntity.getId())).thenReturn(topicExistentEntity);

        final TopicModelInterface topicFoundedModel = this.topicsService.find(topicExistentEntity.getId());

        this.topicsSupport.assertEquals(this.topicsSupport.getModelFixtureMock(0), topicFoundedModel);
    }

    @Test
    public void testFindAbsentList() throws Exception {
        final Integer absentId = 7;
        when(this.topicsMapper.find(absentId)).thenReturn(null);

        final TopicModelInterface topicFoundedModel = this.topicsService.find(absentId);

        Assert.assertNull(topicFoundedModel);
    }

    @Test
    public void testFindWithOptions() throws Exception {
        //TODO: this one test
        Assert.assertEquals(true, false);
    }

    @Test
    public void testFindBySubject() throws Exception {
        final TopicModelInterface topicExistentModel = this.topicsSupport.getModelFixtureMock(0);
        //when()
    }
}
