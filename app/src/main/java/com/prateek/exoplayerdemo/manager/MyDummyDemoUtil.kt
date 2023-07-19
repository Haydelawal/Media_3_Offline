package com.prateek.exoplayerdemo.manager

class MyDummyDemoUtil {

//    // Note: This should be a singleton in your app.
//    databaseProvider = StandaloneDatabaseProvider(context)
//
//// A download cache should not evict media, so should use a NoopCacheEvictor.
//    downloadCache = SimpleCache(downloadDirectory, NoOpCacheEvictor(), databaseProvider)
//
//// Create a factory for reading the data from the network.
//    dataSourceFactory = DefaultHttpDataSource.Factory()
//
//    // Choose an executor for downloading data. Using Runnable::run will cause each download task to
//// download data on its own thread. Passing an executor that uses multiple threads will speed up
//// download tasks that can be split into smaller parts for parallel execution. Applications that
//// already have an executor for background downloads may wish to reuse their existing executor.
//    val downloadExecutor = Executor(Runnable::run)
//
//// Create the download manager.
//    downloadManager =
//    DownloadManager(context, databaseProvider, downloadCache, dataSourceFactory, downloadExecutor)
//
//// Optionally, properties can be assigned to configure the download manager.
//    downloadManager.requirements = requirements
//    downloadManager.maxParallelDownloads = 3
}