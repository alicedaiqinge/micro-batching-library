# MicroBatchingLibrary

`MicroBatchingLibrary` is a Java library for efficiently processing jobs in micro-batches. It enables submission of individual jobs that are grouped into configurable batches and processed periodically. This is ideal for applications needing to handle high volumes of tasks with scheduled batch processing.

## Features

- **Individual Job Submission**: Submit single jobs that get batched and processed together.
- **Configurable Batch Settings**: Set custom batch size and frequency for processing.
- **Customizable Batch Processor**: Define your own `BatchProcessor` to specify how jobs are processed.
- **Graceful Shutdown**: Ensures all jobs are completed before shutdown.
- **Asynchronous Results**: Uses `CompletableFuture` for asynchronous handling of job results.

## Getting Started

### Prerequisites

- Java 8 or later

### Installation

To use this library, clone the repository and add the Java files to your project.

## Usage

### Step 1: Implement a `BatchProcessor`
### Step 2: Create an Instance of a `MicroBatchingLibrary`
### Step 3: Submit Jobs: library.submitJob(job)
### Step 4: Shutdown the Library: library.shutdown()

