package travel.snapshot.qa.manager.tomcat;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

@RunWith(JUnit4.class)
public class AlreadyStartedTomcatTestCase {

    private static TomcatManager manager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void startContainer() {
        manager = Spacelift.task(TomcatStarter.class).execute().await();
    }

    @AfterClass
    public static void stopContainer() {
        Spacelift.task(manager, TomcatStopper.class).execute().await();
    }

    @Test
    public void alreadyStartedTomcatContainerTest() {

        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(is(instanceOf(ContainerManagerException.class)));

        Spacelift.task(TomcatStarter.class).execute().await();
    }
}
