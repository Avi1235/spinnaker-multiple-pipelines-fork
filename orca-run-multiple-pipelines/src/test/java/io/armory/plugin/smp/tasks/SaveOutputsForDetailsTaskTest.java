package io.armory.plugin.smp.tasks;

import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.PipelineExecution;
import com.netflix.spinnaker.orca.pipeline.model.PipelineExecutionImpl;
import com.netflix.spinnaker.orca.pipeline.model.StageExecutionImpl;
import io.armory.plugin.smp.config.RunMultiplePipelinesOutputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static com.netflix.spinnaker.orca.api.pipeline.models.ExecutionType.PIPELINE;
import static org.junit.jupiter.api.Assertions.*;

public class SaveOutputsForDetailsTaskTest {

    private PipelineExecutionImpl pipeline;

    private final static String testApplication = "test_app";
    private final static String testUser = "test_user";

    @BeforeEach
    public void setup() {
        pipeline = new PipelineExecutionImpl(PIPELINE, testApplication);
        pipeline.setAuthentication(new PipelineExecution.AuthenticationDetails(testUser));
    }

    @Test
    public void shouldSaveOutputsForDetailsTaskFinishSuccessfully() {
        var testPipelineId1 = "test_child_1";
        var testPipelineId2 = "test_child_2";

        var task = new SaveOutputsForDetailsTask();

        var context = new HashMap<String, Object>();
        context.put("orderOfExecutions", List.of(List.of(
                new HashMap<String, Object>() {{
                    put("yamlIdentifier", testPipelineId1);
                    put("arguments", new HashMap<>());
                    put("child_pipeline", testPipelineId1);
                }},
                new HashMap<String, Object>() {{
                    put("yamlIdentifier", testPipelineId2);
                    put("arguments", new HashMap<>());
                    put("child_pipeline", testPipelineId2);
                }}
        )));

        var testOutputId = "test_output";
        var testOutputs = new RunMultiplePipelinesOutputs();
        testOutputs.setId(testOutputId);

        context.put("runMultiplePipelinesOutputs", List.of(testOutputs));

        var stageExecution = new StageExecutionImpl(pipeline, "runMultiplePipelines", context);

        var result = task.execute(stageExecution);

        assertEquals(ExecutionStatus.SUCCEEDED, result.getStatus());
        assertFalse(result.getContext().containsKey("orderOfExecutions"));
        assertFalse(result.getContext().containsKey("runMultiplePipelinesOutputs"));
        assertTrue(result.getOutputs().containsKey("executionsList"));

        var executions = (List<RunMultiplePipelinesOutputs>)result.getOutputs().get("executionsList");
        assertEquals(testOutputId, executions.get(0).getId());
    }
}
