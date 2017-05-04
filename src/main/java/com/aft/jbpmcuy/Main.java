// package com.aft.jbpmcuy;
//
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
//
// import org.apache.log4j.Logger;
// import org.kie.api.runtime.manager.RuntimeEngine;
// import org.kie.api.runtime.manager.RuntimeManager;
//
// import com.aft.jbpmcuy.service.JBPMService;
// import com.aft.jbpmcuy.service.dto.CircuitDTO;
// import com.aft.jbpmcuy.service.dto.CircuitInstanceDTO;
//
// public class Main {
//
// private static Logger logger = Logger.getLogger(Main.class);
//
// public static void main(String[] args) {
//
// JBPMService jBPMService;
// try {
// // Initializing jBPM services (should be constructed once in context
// // and injected into services that need it)
//
// jBPMService = new JBPMService();
//
// RuntimeManager manager = jBPMService.getRuntimeManager();
//
// RuntimeEngine engine = jBPMService.getRuntimeEngine();
// List<CircuitInstanceDTO> userCircuitInstances;
//
// // start all available processes
// logger.info("Starting Circuits: \n");
// for (CircuitDTO circuit : jBPMService.getAllCircuits()) {
// logger.info("starting " + circuit.getCircuitName() + " ...");
// jBPMService.startCircuit(circuit, "jeanne", "x/y/z");
// }
//
// /** Get users tasks **/
// userCircuitInstances = new ArrayList<CircuitInstanceDTO>();
// for (CircuitInstanceDTO userInstance :
// jBPMService.getCircuitInstances("philippe")) {
// userCircuitInstances.add(userInstance);
// logger.info("\n" + userInstance.toString());
//
// }
// logger.info("\n --------------------");
//
// // CircuitStepDTO currentStep = jBPMService.getCircuitInstanceById(
// // Long.valueOf(3), "philippe").getCurrentStep();
//
// /** Get step content **/
// // logger.info("Getting Step content...");
// // jBPMService.getStepContent(currentStep);
//
// /** start a task **/
// // jBPMService.startCurrentStep(currentStep, "philippe");
//
// /** Get users tasks **/
// // userCircuitInstances = new ArrayList<CircuitInstanceDTO>();
// // for (CircuitInstanceDTO userInstance : jBPMService
// // .getCircuitInstances("philippe")) {
// // userCircuitInstances.add(userInstance);
// // logger.info("\n" + userInstance.toString());
// //
// // }
// // logger.info("\n --------------------");
//
// /** Complete a task **/
// // jBPMService.goToNextStep(currentStep, "philippe");
//
// /** Get completed steps **/
// // jBPMService.getCompletedStepsByProcessInstanceId(currentStep
// // .getStepTask().getProcessInstanceId());
//
// /** Get users tasks **/
// userCircuitInstances = new ArrayList<CircuitInstanceDTO>();
// for (CircuitInstanceDTO userInstance :
// jBPMService.getCircuitInstances("franklin")) {
// userCircuitInstances.add(userInstance);
// logger.info("\n" + userInstance.toString());
//
// }
// logger.info("\n --------------------");
//
// //
// // /*******************
// // * GOING TO THE NEXT STEP (continuing the workflow)
// // **********************************/
// // // Let's complete some workflow instance current step: the first
// // of
// // // bake's workflows
// // JBPMService.toNextStep(signatureBookRState,
// // bakeWorkflows.get(0));
// //
// // System.out.println("WORKFLOWS LIST AFTER EXECUTION");
// //
// // // List bake's workflows once more time, considering the runtime
// // state
// // JBPMService.getMyWorkflows("bake", signatureBookRState);
//
// /********************************************
// * SHUTTING-DOWN THE APP
// *********************************************/
//
// // Dispose the runtime engine (release that app-scoped shared
// // resource)
// manager.disposeRuntimeEngine(engine);
//
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// }
// }
