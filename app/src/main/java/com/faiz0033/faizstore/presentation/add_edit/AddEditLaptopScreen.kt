package com.faiz0033.faizstore.presentation.add_edit

import android.net.Uri
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.faiz0033.faizstore.R
import com.faiz0033.faizstore.domain.repository.ImageUploadRepository
import com.faiz0033.faizstore.domain.repository.LaptopRepository
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLaptopScreen(
    laptopRepository: LaptopRepository,
    imageUploadRepository: ImageUploadRepository,
    ownerEmail: String,
    onNavigateBack: () -> Unit,
    laptopId: String? = null,
    modifier: Modifier = Modifier
) {
    val viewModel: AddEditLaptopViewModel = viewModel(
        factory = AddEditLaptopViewModel.Factory(laptopRepository, imageUploadRepository, ownerEmail, laptopId)
    )

    val context = LocalContext.current

    val name by viewModel.name.collectAsState()
    val brand by viewModel.brand.collectAsState()
    val category by viewModel.category.collectAsState()
    val processor by viewModel.processor.collectAsState()
    val ram by viewModel.ram.collectAsState()
    val storage by viewModel.storage.collectAsState()
    val price by viewModel.price.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val description by viewModel.description.collectAsState()

    val isSaving by viewModel.isSaving.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                viewModel.setImageBytes(bytes)
                
                try {
                    val imagesDir = java.io.File(context.filesDir, "laptop_images")
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs()
                    }
                    val file = java.io.File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
                    file.writeBytes(bytes)
                    viewModel.setLocalImagePath(file.absolutePath)
                } catch (e: Exception) {
                    // Ignore caching errors
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collectLatest { success ->
            if (success) {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (laptopId == null) stringResource(R.string.add_laptop) 
                        else stringResource(R.string.edit_laptop)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedButton(
                    onClick = { 
                        imagePickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedImageUri != null) stringResource(R.string.image_selected) else stringResource(R.string.select_image))
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.name.value = it },
                    label = { Text(stringResource(R.string.name_req)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { viewModel.brand.value = it },
                    label = { Text(stringResource(R.string.brand_req)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { viewModel.category.value = it },
                    label = { Text(stringResource(R.string.category)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { viewModel.price.value = it },
                        label = { Text(stringResource(R.string.price_req)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { viewModel.stock.value = it },
                        label = { Text(stringResource(R.string.stock)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = processor,
                    onValueChange = { viewModel.processor.value = it },
                    label = { Text(stringResource(R.string.processor)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ram,
                        onValueChange = { viewModel.ram.value = it },
                        label = { Text(stringResource(R.string.ram)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = storage,
                        onValueChange = { viewModel.storage.value = it },
                        label = { Text(stringResource(R.string.storage)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { 
                        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork = connectivityManager.activeNetwork
                        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                        val isOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                        
                        viewModel.saveLaptop(isOnline) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    Text(if (isSaving) stringResource(R.string.saving) else stringResource(R.string.save_laptop))
                }
            }

            if (isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(if (isUploadingImage) stringResource(R.string.uploading_image) else stringResource(R.string.saving_to_database))
                        }
                    }
                }
            }
        }
    }
}
