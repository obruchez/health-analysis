package org.bruchez.olivier.healthanalysis

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.{Sheets, SheetsScopes}

import java.io.{FileInputStream, InputStreamReader}
import java.nio.file.Path
import java.util.Collections

trait SheetsServiceBuilder {
  protected def applicationName: String = "health-analysis"
  protected def credentialsFile: Path
  protected def tokensDirectory: Path
  protected def persistenceUser: String = applicationName

  private val jsonFactory = GsonFactory.getDefaultInstance
  private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)

  private def getCredentials(httpTransport: NetHttpTransport): Credential = {
    val fileInputStream = new FileInputStream(credentialsFile.toFile)
    val clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(fileInputStream))

    val flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
      .setDataStoreFactory(new FileDataStoreFactory(tokensDirectory.toFile))
      .setAccessType("offline")
      .build()

    val LocalServerReceiverPort = 8888

    val localServerReceiver = new LocalServerReceiver.Builder().setPort(LocalServerReceiverPort).build()

    new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize(persistenceUser)
  }

  def sheetsService(): Sheets = {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport

    new Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
      .setApplicationName(applicationName)
      .build()
  }
}
