export interface createTaskDto {
	title: string;
	description: string;
	status: TaskStatus;
	dueDateTime: string;
}

export enum TaskStatus {
	PENDING = 'PENDING',
	IN_PROGRESS = 'IN_PROGRESS',
	COMPLETED = 'COMPLETED',
}
