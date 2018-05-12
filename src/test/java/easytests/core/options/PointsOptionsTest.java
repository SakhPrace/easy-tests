package easytests.core.options;

import easytests.core.models.PointModelInterface;
import easytests.core.models.QuestionModelInterface;
import easytests.core.models.QuizModelInterface;
import easytests.core.models.SolutionModelInterface;
import easytests.core.models.empty.QuestionModelEmpty;
import easytests.core.models.empty.QuizModelEmpty;
import easytests.core.services.PointsServiceInterface;
import easytests.core.services.QuestionsServiceInterface;
import easytests.core.services.QuizzesServiceInterface;
import easytests.core.services.SolutionsServiceInterface;
import easytests.support.PointsSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author nikitalpopov
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PointsOptionsTest {

    private PointsSupport pointsSupport = new PointsSupport();

    private PointsOptionsInterface pointsOptions;

    private PointsServiceInterface pointsService;

    private PointModelInterface pointModel;

    private QuestionModelInterface questionModel;

    private List<QuestionModelInterface> questionsModels;

    private QuestionsServiceInterface questionsService;

    private QuestionsOptionsInterface questionsOptions;

    private QuizzesOptionsInterface quizzesOptions;

    private QuizzesServiceInterface quizzesService;

    private SolutionModelInterface solutionModel;

    private List<SolutionModelInterface> solutionsModels;

    private List<List<SolutionModelInterface>> solutionsModelsLists;

    private SolutionsServiceInterface solutionsService;

    private SolutionsOptionsInterface solutionsOptions;

    private List<PointModelInterface> pointsModels;

    private ArgumentCaptor<List> listCaptor;

    @Before
    public void before() {
        this.questionsService = Mockito.mock(QuestionsServiceInterface.class);
        this.questionsOptions = Mockito.mock(QuestionsOptionsInterface.class);
        this.quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        this.quizzesOptions = Mockito.mock(QuizzesOptionsInterface.class);
        this.solutionsService = Mockito.mock(SolutionsServiceInterface.class);
        this.solutionsOptions = Mockito.mock(SolutionsOptionsInterface.class);
        this.pointsService = Mockito.mock(PointsServiceInterface.class);

        this.pointsOptions = new PointsOptions();
        this.pointsOptions.setPointsService(this.pointsService);
        this.pointsOptions.setQuestionsService(this.questionsService);
        this.pointsOptions.setQuizzesService(this.quizzesService);
        this.pointsOptions.setSolutionsService(this.solutionsService);

        this.listCaptor = ArgumentCaptor.forClass(List.class);
    }

    private PointsOptionsTest withPointModel() {
        this.pointModel = this.pointsSupport.getModelFixtureMock(0);
        return this;
    }

    private PointsOptionsTest withSolutionsModelsFounded() {
        this.solutionsModels = new ArrayList<>();
        when(this.solutionsService.findByPoint(this.pointModel, this.solutionsOptions)).thenReturn(this.solutionsModels);
        return this;
    }

    private PointsOptionsTest withSolutionsModelsInjected() {
        this.solutionsModels = new ArrayList<>();
        when(this.pointModel.getSolutions()).thenReturn(this.solutionsModels);
        return this;
    }


    private PointsOptionsTest withSolutions() {
        this.pointsOptions.withSolutions(this.solutionsOptions);
        return this;
    }

    private PointsOptionsTest withPointsList() {
        this.pointsModels = new ArrayList<>(2);
        this.pointsModels.add(this.pointsSupport.getModelFixtureMock(0));
        this.pointsModels.add(this.pointsSupport.getModelFixtureMock(1));

        return this;
    }

    private PointsOptionsTest withSolutionsModelsListsFounded() {
        this.solutionsModelsLists = new ArrayList<>(2);
        this.solutionsModelsLists.add(new ArrayList<>());
        this.solutionsModelsLists.add(new ArrayList<>());
        when(this.solutionsService.findByPoint(this.pointsModels.get(0), this.solutionsOptions)).thenReturn(solutionsModelsLists.get(0));
        when(this.solutionsService.findByPoint(this.pointsModels.get(1), this.solutionsOptions)).thenReturn(solutionsModelsLists.get(1));

        return this;
    }

    @Test
    public void testWithNoRelations() throws Exception {
        this.withPointModel().withSolutionsModelsFounded();

        final PointModelInterface pointModelWithRelations = this.pointsOptions.withRelations(this.pointModel);

        Assert.assertSame(pointModel, pointModelWithRelations);
        verify(this.solutionsService, times(0)).findByPoint(any(), any());
        verify(this.pointModel, times(0)).setSolutions(anyList());
    }

    @Test
    public void testWithSubjectsRelations() throws Exception {
        this.withPointModel().withSolutionsModelsFounded().withSolutions();

        final PointModelInterface userModelWithRelations = this.usersOptions.withRelations(this.userModel);

        Assert.assertSame(userModel, userModelWithRelations);
        verify(this.subjectsService, times(1)).findByUser(this.userModel, this.subjectsOptions);
        verify(this.userModel, times(1)).setSubjects(this.listCaptor.capture());
        Assert.assertSame(this.subjectsModels, this.listCaptor.getValue());
    }

    @Test
    public void testWithRelationsOnNull() throws Exception {

        final PointsOptionsInterface pointsOptions = new PointsOptions();

        final QuizzesServiceInterface quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        final QuestionsServiceInterface questionsService = Mockito.mock(QuestionsServiceInterface.class);
        final SolutionsServiceInterface solutionsService = Mockito.mock(SolutionsServiceInterface.class);

        pointsOptions.setQuizzesService(quizzesService);
        pointsOptions.setQuestionsService(questionsService);
        pointsOptions.setSolutionsService(solutionsService);

        final QuizzesOptionsInterface quizOptions = Mockito.mock(QuizzesOptionsInterface.class);
        final QuestionsOptionsInterface questionOptions = Mockito.mock(QuestionsOptionsInterface.class);
        final SolutionsOptionsInterface solutionsOptions = Mockito.mock(SolutionsOptionsInterface.class);

        pointsOptions.withQuiz(quizOptions).withQuestion(questionOptions).withSolutions(solutionsOptions);

        final PointModelInterface nullPointModel = null;
        final PointModelInterface pointModelWithRelations = pointsOptions.withRelations(nullPointModel);

        Assert.assertNull(pointModelWithRelations);

    }

    @Test
    public void testWithRelationsOnModelsList() throws Exception {

        final PointModelInterface pointModelFirst = Mockito.mock(PointModelInterface.class);
        final PointModelInterface pointModelSecond = Mockito.mock(PointModelInterface.class);
        pointModelFirst.setId(1);
        pointModelSecond.setId(2);
        given(pointModelFirst.getQuiz()).willReturn(new QuizModelEmpty(1));
        given(pointModelSecond.getQuiz()).willReturn(new QuizModelEmpty(2));
        given(pointModelFirst.getQuestion()).willReturn(new QuestionModelEmpty(1));
        given(pointModelSecond.getQuestion()).willReturn(new QuestionModelEmpty(2));

        final List<PointModelInterface> pointsModels = new ArrayList<>();
        pointsModels.add(pointModelFirst);
        pointsModels.add(pointModelSecond);

        final PointsOptionsInterface pointsOptions = new PointsOptions();

        final QuizzesServiceInterface quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        final QuestionsServiceInterface questionsService = Mockito.mock(QuestionsServiceInterface.class);
        final SolutionsServiceInterface solutionsService = Mockito.mock(SolutionsServiceInterface.class);

        pointsOptions.setQuizzesService(quizzesService);
        pointsOptions.setQuestionsService(questionsService);
        pointsOptions.setSolutionsService(solutionsService);

        final QuizzesOptionsInterface quizOptions = Mockito.mock(QuizzesOptionsInterface.class);
        final QuestionsOptionsInterface questionOptions = Mockito.mock(QuestionsOptionsInterface.class);
        final SolutionsOptionsInterface solutionsOptions = Mockito.mock(SolutionsOptionsInterface.class);

        pointsOptions.withQuiz(quizOptions);
        pointsOptions.withQuestion(questionOptions);
        pointsOptions.withSolutions(solutionsOptions);

        final QuizModelInterface quizModelFirst = Mockito.mock(QuizModelInterface.class);
        final QuizModelInterface quizModelSecond = Mockito.mock(QuizModelInterface.class);
        given(quizzesService.find(1, quizOptions)).willReturn(quizModelFirst);
        given(quizzesService.find(2, quizOptions)).willReturn(quizModelSecond);

        final QuestionModelInterface questionModelFirst = Mockito.mock(QuestionModelInterface.class);
        final QuestionModelInterface questionModelSecond = Mockito.mock(QuestionModelInterface.class);
        given(questionsService.find(1, questionOptions)).willReturn(questionModelFirst);
        given(questionsService.find(2, questionOptions)).willReturn(questionModelSecond);

        final List<SolutionModelInterface> solutionsModelsFirst = new ArrayList<>();
        solutionsModelsFirst.add(Mockito.mock(SolutionModelInterface.class));
        final List<SolutionModelInterface> solutionsModelsSecond = new ArrayList<>();
        solutionsModelsSecond.add(Mockito.mock(SolutionModelInterface.class));
        solutionsModelsSecond.add(Mockito.mock(SolutionModelInterface.class));

        given(solutionsService.findByPoint(pointModelFirst, solutionsOptions)).willReturn(solutionsModelsFirst);
        given(solutionsService.findByPoint(pointModelSecond, solutionsOptions)).willReturn(solutionsModelsSecond);

        final List<PointModelInterface> pointsModelsWithRelations = pointsOptions.withRelations(pointsModels);

        Assert.assertEquals(pointsModels, pointsModelsWithRelations);

        verify(quizzesService).find(1, quizOptions);
        verify(questionsService).find(1, questionOptions);
        verify(solutionsService).findByPoint(pointModelFirst, solutionsOptions);

        verify(pointsModels.get(0)).setQuiz(quizModelFirst);
        verify(pointsModels.get(0)).setQuestion(questionModelFirst);
        verify(pointsModels.get(0)).setSolutions(solutionsModelsFirst);
        verify(pointsModels.get(0)).setSolutions(Mockito.anyList());

        verify(quizzesService).find(2, quizOptions);
        verify(questionsService).find(2, questionOptions);
        verify(solutionsService).findByPoint(pointModelSecond, solutionsOptions);

        verify(pointsModels.get(1)).setQuiz(quizModelSecond);
        verify(pointsModels.get(1)).setQuestion(questionModelSecond);
        verify(pointsModels.get(1)).setSolutions(solutionsModelsSecond);
        verify(pointsModels.get(1)).setSolutions(Mockito.anyList());

    }

    @Test
    public void testSaveWithRelations() throws Exception {

        final PointModelInterface pointModel = Mockito.mock(PointModelInterface.class);
        final PointsServiceInterface pointsService = Mockito.mock(PointsServiceInterface.class);
        final PointsOptionsInterface pointsOptions = new PointsOptions();

        final QuizzesServiceInterface quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        final SolutionsServiceInterface solutionsService = Mockito.mock(SolutionsServiceInterface.class);

        pointsOptions.setPointsService(pointsService);
        pointsOptions.setQuizzesService(quizzesService);
        pointsOptions.setSolutionsService(solutionsService);

        final QuizzesOptionsInterface quizOptions = Mockito.mock(QuizzesOptionsInterface.class);
        final SolutionsOptionsInterface solutionsOptions = Mockito.mock(SolutionsOptionsInterface.class);

        pointsOptions.withQuiz(quizOptions);
        pointsOptions.withSolutions(solutionsOptions);

        final QuizModelInterface quizModel = Mockito.mock(QuizModelInterface.class);
        final List<SolutionModelInterface> solutionsModels = new ArrayList<>();

        solutionsModels.add(Mockito.mock(SolutionModelInterface.class));

        given(pointModel.getQuiz()).willReturn(quizModel);
        given(pointModel.getSolutions()).willReturn(solutionsModels);

        final InOrder inOrder = Mockito.inOrder(quizzesService, pointsService, solutionsService);

        pointsOptions.saveWithRelations(pointModel);

        inOrder.verify(quizzesService).save(quizModel, quizOptions);
        inOrder.verify(pointsService).save(pointModel);
        inOrder.verify(solutionsService).save(solutionsModels, solutionsOptions);

    }

    @Test
    public void testDeleteWithRelations() throws Exception {

        final PointModelInterface pointModel = Mockito.mock(PointModelInterface.class);
        final PointsServiceInterface pointsService = Mockito.mock(PointsServiceInterface.class);
        final PointsOptionsInterface pointsOptions = new PointsOptions();

        final QuizzesServiceInterface quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        final SolutionsServiceInterface solutionsService = Mockito.mock(SolutionsServiceInterface.class);

        pointsOptions.setPointsService(pointsService);
        pointsOptions.setQuizzesService(quizzesService);
        pointsOptions.setSolutionsService(solutionsService);

        final QuizzesOptionsInterface quizOptions = Mockito.mock(QuizzesOptionsInterface.class);
        final SolutionsOptionsInterface solutionsOptions = Mockito.mock(SolutionsOptionsInterface.class);

        pointsOptions.withQuiz(quizOptions);
        pointsOptions.withSolutions(solutionsOptions);

        final QuizModelInterface quizModel = Mockito.mock(QuizModelInterface.class);
        final List<SolutionModelInterface> solutionsModels = new ArrayList<>();

        solutionsModels.add(Mockito.mock(SolutionModelInterface.class));

        given(pointModel.getQuiz()).willReturn(quizModel);
        given(pointModel.getSolutions()).willReturn(solutionsModels);

        final InOrder inOrder = Mockito.inOrder(solutionsService, pointsService, quizzesService);

        pointsOptions.deleteWithRelations(pointModel);

        inOrder.verify(solutionsService).delete(solutionsModels, solutionsOptions);
        inOrder.verify(pointsService).delete(pointModel);
        inOrder.verify(quizzesService).delete(quizModel, quizOptions);

    }

    @Test
    public void testSaveDeleteWithQuiz() {

        final PointsOptionsInterface pointsOptions = new PointsOptions();

        final PointsServiceInterface pointsService = Mockito.mock(PointsServiceInterface.class);
        pointsOptions.setPointsService(pointsService);

        final QuizzesServiceInterface quizzesService = Mockito.mock(QuizzesServiceInterface.class);
        pointsOptions.setQuizzesService(quizzesService);

        final QuizzesOptionsInterface quizzesOptions = Mockito.mock(QuizzesOptionsInterface.class);
        pointsOptions.withQuiz(quizzesOptions);

        final PointModelInterface pointModel = Mockito.mock(PointModelInterface.class);

        final PointsOptionsInterface pointsOptionsSpy = Mockito.spy(pointsOptions);

        pointsOptionsSpy.deleteWithRelations(pointModel);

        verify(quizzesService, times(1)).delete(pointModel.getQuiz(), quizzesOptions);

        pointsOptionsSpy.saveWithRelations(pointModel);

        verify(quizzesService, times(1)).save(pointModel.getQuiz(), quizzesOptions);

    }

}
