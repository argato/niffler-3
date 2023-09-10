package guru.qa.niffler.jupiter;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.AllureId;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UserQueueExtension
    implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(
      UserQueueExtension.class);

  private static Map<User.UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

  static {
    Queue<UserJson> usersWithFriends = new ConcurrentLinkedQueue<>();
    usersWithFriends.add(bindUser("dima", "12345"));
    usersWithFriends.add(bindUser("barsik", "12345"));
    usersQueue.put(User.UserType.WITH_FRIENDS, usersWithFriends);
    Queue<UserJson> usersInSent = new ConcurrentLinkedQueue<>();
    usersInSent.add(bindUser("bee", "12345"));
    usersInSent.add(bindUser("anna", "12345"));
    usersQueue.put(User.UserType.INVITATION_SENT, usersInSent);
    Queue<UserJson> usersInRc = new ConcurrentLinkedQueue<>();
    usersInRc.add(bindUser("valentin", "12345"));
    usersInRc.add(bindUser("pizzly", "12345"));
    usersQueue.put(User.UserType.INVITATION_RECEIVED, usersInRc);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    List<Method> handleMethods = new ArrayList<>();
    handleMethods.add(context.getRequiredTestMethod());
    Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(BeforeEach.class))
          .forEach(handleMethods::add);

    List<Parameter> handleParameters = handleMethods.stream()
                                                    .map(Executable::getParameters)
                                                    .flatMap(Arrays::stream)
                                                    .filter(parameter -> parameter.getType()
                                                                                  .isAssignableFrom(
                                                                                      UserJson.class))
                                                    .filter(
                                                        parameter -> parameter.isAnnotationPresent(
                                                            User.class))
                                                    .toList();

    for (Parameter parameter : handleParameters) {
      if (parameter.getType().isAssignableFrom(UserJson.class)) {
        User parameterAnnotation = parameter.getAnnotation(User.class);
        User.UserType userType = parameterAnnotation.userType();
        Queue<UserJson> usersQueueByType = usersQueue.get(parameterAnnotation.userType());
        UserJson candidateForTest = null;
        while (candidateForTest == null) {
          candidateForTest = usersQueueByType.poll();
        }
        candidateForTest.setUserType(userType);
        context.getStore(NAMESPACE).put(generateKey(context, parameter), candidateForTest);
      }
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    List<Method> handleMethods = new ArrayList<>();
    handleMethods.add(context.getRequiredTestMethod());
    Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(BeforeEach.class))
          .forEach(handleMethods::add);

    List<Parameter> handleParameters = handleMethods.stream()
                                                    .map(Executable::getParameters)
                                                    .flatMap(Arrays::stream)
                                                    .filter(parameter -> parameter.getType()
                                                                                  .isAssignableFrom(
                                                                                      UserJson.class))
                                                    .filter(
                                                        parameter -> parameter.isAnnotationPresent(
                                                            User.class))
                                                    .toList();
    for (Parameter parameter : handleParameters) {
      UserJson userFromTest = context.getStore(NAMESPACE).get(generateKey(context, parameter),
                                                              UserJson.class);
      usersQueue.get(userFromTest.getUserType()).add(userFromTest);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)
           && parameterContext.getParameter().isAnnotationPresent(User.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(
        generateKey(extensionContext, parameterContext.getParameter()), UserJson.class);
  }

  private String getAllureId(ExtensionContext context) {
    AllureId allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
    if (allureId == null) {
      throw new IllegalStateException("Annotation @AllureId must be present!");
    }
    return allureId.value();
  }

  private static UserJson bindUser(String username, String password) {
    UserJson user = new UserJson();
    user.setUsername(username);
    user.setPassword(password);
    return user;
  }

  private String generateKey(ExtensionContext context, Parameter parameter) {
    return String.format("id_%s_%s", getAllureId(context), parameter.getName());
  }
}
