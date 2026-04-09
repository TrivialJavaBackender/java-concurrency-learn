package by.pavel.logging;

// TODO: Реализуй AsyncConfig с MDC propagation
// @Configuration
// @EnableAsync
// implements AsyncConfigurer
//
// getAsyncExecutor():
//   ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor()
//   executor.setTaskDecorator(runnable -> {
//       Map<String, String> mdcContext = MDC.getCopyOfContextMap(); // снаружи lambda!
//       return () -> {
//           try {
//               if (mdcContext != null) MDC.setContextMap(mdcContext);
//               runnable.run();
//           } finally {
//               MDC.clear();
//           }
//       };
//   });
