package unl.fct.di.proto1.common.workerService;


public abstract class WorkerRequest {
    static int nextId = 0;

    int id;

    public WorkerRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
