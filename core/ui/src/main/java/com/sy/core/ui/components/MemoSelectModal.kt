import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sy.odokcompose.core.designsystem.OdokColors
import com.sy.odokcompose.core.designsystem.icon.OdokIcons

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MemoSelectModal(
    onCameraBtnClick: () -> Unit,
    onMicroPhoneBtnClick: () -> Unit,
    onSelfTypeBtnClick: () -> Unit,
    onDismissRequest: () -> Unit,
    memoSelectSheetState: SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = memoSelectSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp,),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "책 속의 텍스트를\n가져올 방법을 선택해주세요.",
                fontSize = 16.sp,
                maxLines = 2,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp, bottom = 30.dp)
            )
            Button(
                onClick = {onMicroPhoneBtnClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OdokColors.Black
                ),
            ) {
                Image(
                    painter = painterResource(id = OdokIcons.Microphone),
                    contentDescription = "마이크",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("음성인식으로 가져오기")
            }
            Button(
                onClick = { onCameraBtnClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OdokColors.Black
                ),
            ) {
                Image(
                    painter = painterResource(id = OdokIcons.Camera),
                    contentDescription = "사진",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("사진으로 가져오기")
            }

            Button(
                onClick = {
                    onSelfTypeBtnClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OdokColors.Black
                ),
            ) {
                Image(
                    painter = painterResource(id = OdokIcons.Keyboard),
                    contentDescription = "직접 입력",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("직접 입력하기")
            }
        }
    }
}