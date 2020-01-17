package org.sdoroshenko.concurrency.examples.cf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class OpenSalarySociety {
    private final EmployeeREST employeeREST;
    private final SalaryREST salaryREST;

    public OpenSalarySociety(EmployeeREST employeeREST, SalaryREST salaryREST) {
        this.employeeREST = employeeREST;
        this.salaryREST = salaryREST;
    }

    public static void main(String[] args) {
        OpenSalarySociety salarySociety = new OpenSalarySociety(new EmployeeREST(), new SalaryREST());

        CompletionStage<List<CompletionStage<Employee>>> result = salarySociety.hiredEmployees() // employees list
            .thenApplyAsync(
                list -> list.stream()
                    .map(e -> salarySociety.getSalary(e.id).thenApplyAsync(s -> { // fill employee salary
                        e.salary = s;
                        return e;
                    }))
                    .collect(Collectors.toList())
            );

        List<CompletionStage<Employee>> filled = result.toCompletableFuture().join();
        filled.forEach(e -> e.thenAccept(System.out::println));
    }

    public CompletionStage<List<Employee>> hiredEmployees() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching employees in: " + Thread.currentThread().getName());
            return employeeREST.getEmployees();
        });
    }

    public CompletionStage<Integer> getSalary(final int hiredEmployeeId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching salary for employees: " + hiredEmployeeId + " in: " + Thread.currentThread().getName());
            return salaryREST.getSalary(hiredEmployeeId);
        });
    }

    static class EmployeeREST {
        public List<Employee> getEmployees() {
            return Arrays.asList(
                new Employee(1, "Adam"),
                new Employee(2, "Eve"),
                new Employee(3, "Snake")
            );
        }
    }

    static class SalaryREST {
        public int getSalary(int id) {
            Map<Integer, Integer> salaryMap = new HashMap<>();
            salaryMap.put(1, 100);
            salaryMap.put(2, 150);
            salaryMap.put(3, 200);
            return salaryMap.get(id);
        }
    }

    static class Employee {
        int id;
        String name;
        int salary;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Employee.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("salary=" + salary)
                .toString();
        }
    }
}
