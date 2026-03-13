from fastapi import FastAPI
from pydantic import BaseModel
import random

app = FastAPI(title="Booking AI Server")

class ReservationData(BaseModel):
    reservation_id: int
    hotel_id: int
    room_id: int
    user_id: int
    days: int

@app.post("/api/v1/ai/predict-cancel")
async def predict_cancel_probability(data: ReservationData):
    # 더미 AI 모델: 유저나 호텔 데이터에 기반한 취소 확률 계산 (Mock)
    # 실제 환경에서는 scikit-learn 모델 덤프 등을 불러와서 예측
    
    # 0% ~ 100% 사이의 랜덤 취소 확률 생성
    probability = round(random.uniform(0.01, 0.99), 2)
    
    return {
        "reservation_id": data.reservation_id,
        "ai_cancel_prob": probability,
        "message": "Prediction successful"
    }

@app.get("/health")
async def health_check():
    return {"status": "ok"}
